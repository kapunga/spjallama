package spjallama.core

import spjallama.core.character.Character

case class InstructionBuilder(character: Option[Character] = None, modelOverride: Option[Model] = None, userName: Option[String] = None):
  def withCharacter(character: Character): InstructionBuilder = this.copy(character = Some(character))
  def withModelOverride(model: Model): InstructionBuilder = this.copy(modelOverride = Some(model))
  def withUserName(name: String): InstructionBuilder = this.copy(userName = Some(name))
  
  def buildConversation: Either[Throwable, Conversation] =
    for {
      model <- modelOverride.orElse(character.map(_.model))
        .toRight(new RuntimeException("Can't generate conversation without a model"))
      systemPrompt = buildSystemPrompt.toList
    } yield Conversation(model, List.empty, userName)
    
  private def buildSystemPrompt: Option[Message] =
    for {
      context <- character.map(_.instruction)
      content = context.mkString(" ")
    } yield Message.System(content)
    
    
    
