package spjallama.client.model

import io.circe.syntax.*
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import spjallama.core.{Message, Model}
import spjallama.core.Model.Llama3_2

class ChatRequestSpec extends AnyFlatSpec with Matchers with EitherValues:
  "Given chat request" should "be properly serialized to Json" in {
    val requestNoStream = ChatRequest(model = Llama3_2, messages = Seq(Message.User("why is the sky blue?")))

    val requestNoStreamSerialized = requestNoStream.asJson.spaces2

    requestNoStreamSerialized shouldBe ChatRequestJson.simpleNoStreamingRequest
  }
