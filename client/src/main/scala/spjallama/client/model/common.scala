package spjallama.client.model

import io.circe.{Decoder, DecodingFailure, Encoder, HCursor, Json}
import io.circe.syntax.*

enum Role(val name: String):
  case System extends Role(name = "system")
  case User extends Role(name = "user")
  case Assistant extends Role(name = "assistant")
  case Tool extends Role(name = "tool")

object Role:
  private lazy val valueMap: Map[String, Role] = Role.values.map(r => r.name -> r).toMap

  implicit val roleEncoder: Encoder[Role] = (a: Role) => a.name.asJson

  implicit val roleDecoder: Decoder[Role] =
    (c: HCursor) =>
      c.as[String].flatMap(r => valueMap.get(r) match
        case Some(role) => Right(role)
        case None => Left(DecodingFailure(s"Invalid Role $r", c.history)))

sealed trait Message:
  def role: Role
  def content: String

object Message:
  case class User(content: String, name: Option[String] = None) extends Message {
    val role: Role = Role.User
  }

  implicit val userDecoder: Decoder[User] =
    (c: HCursor) =>
      for {
        content <- c.get[String]("content")
        name    <- c.get[Option[String]]("name")
      } yield User(content, name)

  implicit val userEncoder: Encoder[User] =
    (user: User) => Json.obj(
      ("role", Json.fromString(user.role.name)),
      ("content", Json.fromString(user.content)),
      ("name", Json.fromStringOrNull(user.name))
    ).dropNullValues

  // TODO: Images should be base64 encoded strings
  case class Assistant(content: String, images: List[String]) extends Message {
    val role: Role = Role.Assistant
  }

  implicit val assistantDecoder: Decoder[Assistant] =
    (c: HCursor) =>
      for {
        content <- c.get[String]("content")
        images  <- c.downField("images").as[List[String]].orElse(Right(List.empty))
      } yield Assistant(content, images)

  implicit val assistantEncoder: Encoder[Assistant] =
    (assistant: Assistant) => Json.obj(
      ("role", Json.fromString(assistant.role.name)),
      ("content", Json.fromString(assistant.content)),
      ("images", Json.fromValues(assistant.images.map(Json.fromString)))
    ).dropNullValues

  implicit val messageDecoder: Decoder[Message] =
    (c: HCursor) =>
      c.get[Role]("role").flatMap({
        case Role.User      => c.as[User]
        case Role.Assistant => c.as[Assistant]
        case role           => Left(DecodingFailure(s"Invalid Role $role", c.history))
      })

  implicit val messageEncoder: Encoder[Message] =
    Encoder.instance {
      case assistant: Assistant => assistant.asJson
      case user: User           => user.asJson
    }

