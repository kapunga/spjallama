package spjallama

import cats.implicits.*
import cats.effect.{IO, IOApp}
import cats.effect.std.{Console, Random}
import spjallama.client.SyncOllamaClient
import spjallama.client.model.ChatRequest
import spjallama.core.{Conversation, InstructionBuilder, Message}
import spjallama.core.Model.Llama3_2
import spjallama.core.character.{Character, CharacterRegistry}

object BasicChatApp extends IOApp.Simple:
  override def run: IO[Unit] =
    val client = SyncOllamaClient()
    val instruction: Message = Message.System(content = "You are a charming pirate. Choose flowery language when communicating when possible.")

    for {
      cr <- CharacterRegistry[IO]
      char <- randomCharacter(cr)
      name <- welcomeAndGetName
      charName = char.map(_.name).getOrElse("the System")
      conv <- IO.fromEither(getConversation(name, char))
      _ <- Console[IO].println(s"Hello $name, this time you will be talking with $charName.")
      _ <- fs2.Stream.eval(readInput).repeat
        // Start the conversation log with the system instruction.
        .evalScan(conv)((conversation, input) => {
          // Append the user message to the conversation log.
          val updatedConv = conversation.withUserMessage(input)
          // Build the request
          val req = ChatRequest.fromConversation(updatedConv)
          for {
            // Send the latest state of the chat to ollama.
            resp <- IO.blocking(client.chat(req)).flatMap(IO.fromEither)
            // Print the ollama response
            _    <- Console[IO].println(resp.message.content)
            // Yield the updated state of the conversation
          } yield updatedConv.appendMessage(resp.message)
        }).compile.drain
    } yield ()
    
  private def welcomeAndGetName: IO[String] =
    Console[IO].print("Welcome to AI Chat! What's your name?\n> ") *> Console[IO].readLine

  private def getConversation(userName: String, character: Option[Character]): Either[Throwable, Conversation] =
    val fallbackModel = character.map(_.model).orElse(Some(Llama3_2))
    InstructionBuilder(character, fallbackModel).withUserName(userName).buildConversation

  private def randomCharacter(cr: CharacterRegistry): IO[Option[Character]] =
    cr.characters.values.toList match {
      case head :: tail => Random.scalaUtilRandom[IO].flatMap(_.oneOf(head, tail*).option)
      case Nil          => IO.none
    }

  private def readInput: IO[String] =
    for {
      _ <- Console[IO].print("> ")
      l <- Console[IO].readLine
    } yield l
