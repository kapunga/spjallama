package spjallama.core

import io.circe.{Decoder, Encoder, HCursor}
import io.circe.syntax.*
import spjallama.core.config.{given, *}

trait Model:
  def name: String

object Model:
  case object Llama3_2 extends Model { override val name = "llama3.2" }
  case object Mistral extends Model { override val name = "mistral" }
  case class Custom(name: String) extends Model
  
  def fromString(name: String): Model = name match {
    case Llama3_2.name => Llama3_2
    case Mistral.name  => Mistral
    case customName    => Custom(customName)
  }

  given Decoder[Model] =
    (c: HCursor) => c.as[String].map(fromString)

  given Encoder[Model] = (model: Model) => model.name.asJson
  
  given ConfigFetcher[Model] = (config, key) => config.get[String](key).map(fromString)
