package spjallama.client.model

import io.circe.parser.*
import java.time.Instant
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import spjallama.core.Model
import spjallama.core.Model.Llama3_2

class ChatResponseSpec extends AnyFlatSpec with Matchers with EitherValues:
  "Given chat response" should "be properly deserialized from Json" in {
    val responseNoStream = ChatResponse(
      model = Llama3_2,
      createdAt = Instant.parse("2023-12-12T14:13:43.416799Z"),
      message = Message.Assistant(content = "Hello! How are you today?", images = List.empty),
      done = true,
      totalDuration = Some(5191566416L),
      loadDuration = Some(2154458L),
      promptEvalCount = Some(26L),
      promptEvalDuration = Some(383809000L),
      evalCount = Some(298L),
      evalDuration = Some(4799921000L))

    val responseNoStreamDeserialized = parse(ChatResponseJson.simpleNonStreamingResponse).flatMap(_.as[ChatResponse]).toOption.get

    responseNoStreamDeserialized shouldBe responseNoStream
  }
