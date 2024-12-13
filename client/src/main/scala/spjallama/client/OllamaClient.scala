package spjallama.client

import spjallama.client.model.{ChatRequest, ChatResponse}
import sttp.client4._
import sttp.client4.circe._

trait OllamaClient[F[_]]:
  def chat(request: ChatRequest): F[ChatResponse]

type SyncType[A] = Either[Throwable, A]

class SyncOllamaClient extends OllamaClient[SyncType]:
  val backend: SyncBackend = DefaultSyncBackend()

  def chat(request: ChatRequest): SyncType[ChatResponse] =
    basicRequest
      .post(uri"http://localhost:11434/api/chat")
      .body(request)
      .response(asJson[ChatResponse])
      .send(backend).body
