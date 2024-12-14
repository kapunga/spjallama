package spjallama.client.model

import io.circe.*
import io.circe.Codec.AsObject.derivedConfigured
import io.circe.derivation.Configuration
import io.circe.generic.semiauto.*
import spjallama.core.{Message, Model}

/**
 *
 * https://github.com/ollama/ollama/blob/main/docs/api.md#generate-a-chat-completion
 */
case class ChatRequest(model: Model, messages: Seq[Message], stream: Boolean = false)

object ChatRequest:
  given Configuration = Configuration.default.withSnakeCaseMemberNames
  given Codec[ChatRequest] = derivedConfigured
