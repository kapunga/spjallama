package spjallama.core

case class Conversation(model: Model, chat: List[Message], name: Option[String]):
  def withUserMessage(content: String): Conversation =
    this.copy(chat = chat.appended(Message.User(content = content, name = name)))
  def appendMessage(message: Message): Conversation =
    this.copy(chat = chat.appended(message))

object Conversation:
  trait Context[A]:
    def toInstruction(a: A): List[String]

    extension (a: A)
      def instruction(using ca: Context[A]): List[String] = ca.toInstruction(a)

