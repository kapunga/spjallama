package spjallama

import cats.effect.std.{Console, Queue, Random}
import cats.effect.{IO, IOApp}
import spjallama.client.SyncOllamaClient
import spjallama.client.model.ChatRequest
import spjallama.core.Model.Llama3_2
import spjallama.core.{Conversation, InstructionBuilder}
import spjallama.core.character.{Character, CharacterRegistry}

object PingPongApp extends IOApp.Simple:
  type ChatQueue = Queue[IO, Option[String]]
  val ollamaClient = SyncOllamaClient()

  override def run: IO[Unit] =
    for {
      characters <- CharacterRegistry[IO].map(_.characters.values.toList)
      (charA, charB) <- pickTwoCharacters(characters)
      convA <- IO.fromEither(getConversation(charB.name, Some(charA)))
      convB <- IO.fromEither(getConversation(charA.name, Some(charB)))
      queueA <- Queue.bounded[IO, Option[String]](20)
      queueB <- Queue.bounded[IO, Option[String]](20)
      introB <- characterIntro(convB)
      _ <- queueA.offer(Some(introB))
      streamA = makePipe(queueA, queueB, convA, convA.name.getOrElse("Assistant A"))
      streamB = makePipe(queueB, queueA, convB, convB.name.getOrElse("Assistant B"))
      _ <- streamA.concurrently(streamB).compile.drain
    } yield ()

  private def pickTwoCharacters(characters: List[Character]): IO[(Character, Character)] =
    for {
      _    <- IO.raiseWhen(characters.size < 2)(new RuntimeException("Not enough characters, need at least 2 to play."))
      list <- Random.scalaUtilRandom[IO].flatMap(_.shuffleList(characters))
    } yield (list.head, list.tail.head)

  private def characterIntro(conversation: Conversation): IO[String] =
    val c = conversation.withUserMessage("Please introduce yourself in character.")
    val req = ChatRequest.fromConversation(c)
    IO.blocking(ollamaClient.chat(req)).flatMap(IO.fromEither).map(_.message.content)

  private def getConversation(userName: String, character: Option[Character]): Either[Throwable, Conversation] =
    val fallbackModel = character.map(_.model).orElse(Some(Llama3_2))
    InstructionBuilder(character, fallbackModel).withUserName(userName).buildConversation

  private def makePipe(source: ChatQueue, dest: ChatQueue, conversation: Conversation, partnerName: String): fs2.Stream[IO, Conversation] =
    val name = conversation.name.get

    fs2.Stream.fromQueueNoneTerminated(source, 10).evalScan(conversation)((conv, input) => {
      val updatedConv = conv.withUserMessage(input)
      val req = ChatRequest.fromConversation(updatedConv)
      for {
        _ <- Console[IO].println(s"${partnerName.toUpperCase} - $input\n")
        resp <- IO.blocking(ollamaClient.chat(req)).flatMap(IO.fromEither)
        _ <- dest.offer(Some(resp.message.content))
      } yield updatedConv.appendMessage(resp.message)
    })
