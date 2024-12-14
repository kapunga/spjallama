package spjallama.client.model

import io.circe.Codec.AsObject.derivedConfigured
import io.circe.derivation.Configuration
import io.circe.{Codec, Decoder, Encoder}
import java.time.Instant
import spjallama.core.{Message, Model}

/**
 * https://github.com/ollama/ollama/blob/main/docs/api.md#generate-a-chat-completion
 */
case class ChatResponse(model: Model,
                        createdAt: Instant,
                        message: Message,
                        done: Boolean,
                        totalDuration: Option[Long],
                        loadDuration: Option[Long],
                        promptEvalCount: Option[Long],
                        promptEvalDuration: Option[Long],
                        evalCount: Option[Long],
                        evalDuration: Option[Long])

object ChatResponse:
  given Configuration = Configuration.default.withSnakeCaseMemberNames
  given Codec[ChatResponse] = derivedConfigured[ChatResponse]