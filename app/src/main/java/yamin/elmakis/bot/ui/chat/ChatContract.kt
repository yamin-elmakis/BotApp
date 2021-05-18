package yamin.elmakis.bot.ui.chat

import androidx.lifecycle.LiveData
import yamin.elmakis.domain.entity.ChatMessage
import yamin.elmakis.domain.entity.InputType
import yamin.elmakis.domain.entity.SelectionOption

interface ChatContract {
    interface ChatViewModel {
        fun getStateLiveData(): LiveData<State>
        fun getEventsLiveData(): LiveData<Event>

        fun sendText(text: String)
        fun sendOption(option: SelectionOption)
    }

    data class State(
        val messages: List<ChatMessage> = listOf(),
        val inputType: InputType = InputType.Text
    )

    enum class Event {
        ERROR, HIDE_KEYBOARD, EMPTY_TEXT
    }
}