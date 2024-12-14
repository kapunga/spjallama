package spjallama.core.character

import spjallama.core.Model
import spjallama.core.config.{*, given}

case class Character(name: String, model: Model, persona: Persona)
object Character:
  given ConfigExtractor[Character] = config =>
    for {
      name <- config.get[String]("name")
      model <- config.get[Model]("model")
      persona <- config.get[Persona]("persona")
    } yield Character(name, model, persona)

case class Persona(description: String)
object Persona:
  given ConfigExtractor[Persona] = config => 
    for {
      description <- config.get[String]("description")
    } yield Persona(description)
