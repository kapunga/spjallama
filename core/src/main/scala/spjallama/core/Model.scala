package spjallama.core

import io.circe.{Decoder, Encoder, HCursor}
import io.circe.syntax.*

trait Model:
  def name: String

object Model:
  case object Llama3_2 extends Model { override val name = "llama3.2" }
  case object Mistral extends Model { override val name = "mistral" }
  case class Custom(name: String) extends Model

  given Decoder[Model] =
    (c: HCursor) => c.as[String].map({
      case Llama3_2.name => Llama3_2
      case Mistral.name  => Mistral
      case customName    => Custom(customName)
    })

  given Encoder[Model] = (model: Model) => model.name.asJson
