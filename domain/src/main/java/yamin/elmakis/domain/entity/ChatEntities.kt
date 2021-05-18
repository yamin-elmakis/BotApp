package yamin.elmakis.domain.entity

sealed class ChatMessage(val id : Long) {
    data class TextChatMessage(val text: String, val sender: Sender) : ChatMessage(System.nanoTime())
    object SeparatorLine : ChatMessage(System.nanoTime())
}

enum class Sender {
    ME, BOT
}

sealed class InputType {
    object None : InputType()
    object BotTyping : InputType()
    object Text : InputType()
    object Number : InputType()
    data class Selection(val options: List<SelectionOption>? = null) : InputType()
}

enum class SelectionOption {
    YES, NO, RESTART, EXIT
}

data class UserAnswer(val answer: String, val type: AnswerType)

enum class AnswerType {
    TEXT, NUMBER, SELECTION
}