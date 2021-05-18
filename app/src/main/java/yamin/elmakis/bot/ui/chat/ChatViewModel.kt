package yamin.elmakis.bot.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import yamin.elmakis.bot.base.BaseViewModel
import yamin.elmakis.bot.ui.chat.ChatContract.*
import yamin.elmakis.bot.util.MutableSafeLiveData
import yamin.elmakis.bot.util.SingleLiveData
import yamin.elmakis.domain.entity.*
import yamin.elmakis.domain.usecase.ChatUseCase

class ChatViewModel(private val chatUseCase: ChatUseCase) : BaseViewModel(), ChatContract.ChatViewModel {

    private val state = MutableSafeLiveData(State())
    private val events = SingleLiveData<Event>()

    init {
        chatUseCase.getChatStream()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onNext = { message ->
                val updatedMessages: List<ChatMessage> =
                    state.value.messages.toMutableList().also { it.add(message) }
                state.value = state.value.copy(messages = updatedMessages)
            }, onError = {
                Log.e("YAMIN", "MainViewModel:chat: ${it.message}", it)
                events.postValue(Event.ERROR)
            }).addToDispose()

        chatUseCase.getInputStream()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onNext = { input ->
                state.value = state.value.copy(inputType = input)
            }, onError = {
                Log.e("YAMIN", "MainViewModel:input: ${it.message}", it)
                events.postValue(Event.ERROR)
            }).addToDispose()
    }

    override fun getStateLiveData(): LiveData<State> {
        return state
    }

    override fun getEventsLiveData(): LiveData<Event> {
        return events
    }

    override fun sendText(text: String) {
        if (text.isNotEmpty()) {
            when (state.value.inputType) {
                InputType.Number -> chatUseCase.sendUserAnswer(UserAnswer(text, AnswerType.NUMBER))
                InputType.Text -> chatUseCase.sendUserAnswer(UserAnswer(text, AnswerType.TEXT))
            }
            events.value = Event.HIDE_KEYBOARD
        } else {
            events.value = Event.EMPTY_TEXT
        }
    }

    override fun sendOption(option: SelectionOption) {
        chatUseCase.sendUserAnswer(UserAnswer(option.name, AnswerType.SELECTION))
        events.value = Event.HIDE_KEYBOARD
    }
}