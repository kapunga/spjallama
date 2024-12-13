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

  given Encoder[Role] = (a: Role) => a.name.asJson

  given Decoder[Role] = (c: HCursor) =>
    c.as[String].flatMap(r => valueMap.get(r).toRight(DecodingFailure(s"Invalid Role $r", c.history)))

sealed trait Message:
  def role: Role
  def content: String

object Message:
  case class User(content: String, name: Option[String] = None) extends Message {
    val role: Role = Role.User
  }

  given Decoder[User] =
    (c: HCursor) =>
      for {
        content <- c.get[String]("content")
        name    <- c.get[Option[String]]("name")
      } yield User(content, name)

  given Encoder[User] =
    (user: User) => Json.obj(
      ("role", Json.fromString(user.role.name)),
      ("content", Json.fromString(user.content)),
      ("name", Json.fromStringOrNull(user.name))
    ).dropNullValues

  // TODO: Images should be base64 encoded strings
  case class Assistant(content: String, images: List[String]) extends Message {
    val role: Role = Role.Assistant
  }

  given Decoder[Assistant] =
    (c: HCursor) =>
      for {
        content <- c.get[String]("content")
        images  <- c.downField("images").as[List[String]].orElse(Right(List.empty))
      } yield Assistant(content, images)

  given Encoder[Assistant] =
    (assistant: Assistant) => Json.obj(
      ("role", Json.fromString(assistant.role.name)),
      ("content", Json.fromString(assistant.content)),
      ("images", Json.fromValues(assistant.images.map(Json.fromString)))
    ).dropNullValues

  case class System(content: String) extends Message {
    val role: Role = Role.System
  }

  given Decoder[System] =
    _.downField("content").as[String].map(System.apply)

  given Encoder[System] =
    (system: System) => Json.obj(
      ("role", Json.fromString(system.role.name)),
      ("content", Json.fromString(system.content))
    )

  given Decoder[Message] =
    (c: HCursor) =>
      c.get[Role]("role").flatMap({
        case Role.Assistant => c.as[Assistant]
        case Role.User      => c.as[User]
        case Role.System    => c.as[System]
        case role           => Left(DecodingFailure(s"Invalid Role $role", c.history))
      })

  given Encoder[Message] =
    Encoder.instance {
      case assistant: Assistant => assistant.asJson
      case system: System       => system.asJson
      case user: User           => user.asJson
    }

