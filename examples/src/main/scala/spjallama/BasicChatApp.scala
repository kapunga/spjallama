package spjallama

import cats.effect.{IO, IOApp}
import cats.effect.std.Console
import spjallama.client.SyncOllamaClient
import spjallama.client.model.{ChatRequest, Message}
import spjallama.core.Model.Mistral

object BasicChatApp extends IOApp.Simple:
  override def run: IO[Unit] =
    val client = SyncOllamaClient()
    val instruction: Message = Message.System(content = "You are a charming pirate. Choose flowery language when communicating when possible.")

    for {
      _ <- Console[IO].println("Get ready for a chat with a pirate!")
      _ <- fs2.Stream.eval(readInput).repeat
        // Start the conversation log with the system instruction.
        .evalScan(Seq(instruction))((msgs, input) => {
          // Make a new user message from the most recent Console input.
          val userMsg: Message = Message.User(content = input)
          // Append the user message to the conversation log.
          val updatedMsgs = msgs.appended(userMsg)
          // Build the request
          val req = ChatRequest(model = Mistral, messages = updatedMsgs)
          for {
            // Send the latest state of the chat to ollama.
            resp <- IO.blocking(client.chat(req)).flatMap(IO.fromEither)
            // Print the ollama response
            _    <- Console[IO].println(resp.message.content)
            // Yield the updated state of the conversation
          } yield updatedMsgs.appended(resp.message)
        }).compile.drain
    } yield ()

  private def readInput: IO[String] =
    for {
      _ <- Console[IO].print("> ")
      l <- Console[IO].readLine
    } yield l
