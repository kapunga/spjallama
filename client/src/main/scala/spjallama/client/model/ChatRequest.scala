package spjallama.client.model

import io.circe.*
import io.circe.derivation.Configuration
import io.circe.generic.semiauto.*
import spjallama.core.Model

/**
 *
 * https://github.com/ollama/ollama/blob/main/docs/api.md#generate-a-chat-completion
 */
case class ChatRequest(model: Model, messages: Seq[Message], stream: Boolean = false)

object ChatRequest:
  implicit val customConfig: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val chatRequestDecoder: Decoder[ChatRequest] = deriveDecoder
  implicit val chatRequestEncoder: Encoder[ChatRequest] = deriveEncoder
