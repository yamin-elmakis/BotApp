package yamin.elmakis.repository

import android.util.Log
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.ReplaySubject
import yamin.elmakis.domain.entity.*
import yamin.elmakis.domain.repo.ChatRepository
import yamin.elmakis.repository.data.response.BotResponse
import yamin.elmakis.repository.data.source.RemoteData
import yamin.elmakis.repository.util.ParserHelper

class ChatRepositoryImpl(
    private val remoteData: RemoteData,
    private val parser: ParserHelper
) : ChatRepository {

    // in case of more screens, send all messages in case re-connect
    private val messages: ReplaySubject<ChatMessage> =ReplaySubject.create()
    // show only current input state
    private val input: BehaviorSubject<InputType> = BehaviorSubject.create()
    private val compositeDisposable = CompositeDisposable()

    init {
        startChat()
    }

    private fun startChat() {
        messages.onNext(ChatMessage.TextChatMessage("Hello, I am Yamin!", Sender.BOT))
        messages.onNext(ChatMessage.TextChatMessage("What is your name?", Sender.BOT))
        input.onNext(InputType.Text)
    }

    override fun getChatStream(): Observable<ChatMessage> {
        return messages
    }

    override fun getInputStream(): Observable<InputType> {
        return input
    }

    override fun sendUserAnswer(userAnswer: UserAnswer) {
        messages.onNext(ChatMessage.TextChatMessage(userAnswer.answer, Sender.ME))
        if (userAnswer.type == AnswerType.SELECTION && SelectionOption.valueOf(userAnswer.answer) == SelectionOption.RESTART) {
            messages.onNext(ChatMessage.SeparatorLine)
            startChat()
            return
        }
        input.onNext(InputType.BotTyping)
        compositeDisposable.add(
            remoteData.sendUserAnswer(parser.toJson(userAnswer))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeBy(onSuccess = {
                    handleMessageResponse(it)
                }, onError = {
                    Log.e("YAMIN", "ChatRepositoryImpl:sendText: error: ${it.message}", it)
                })
        )
    }

    private fun handleMessageResponse(messageStr: String) {
        val message: BotResponse? =
            try {
                val typeToken = TypeToken.get(BotResponse::class.java).type
                parser.fromJson<BotResponse>(messageStr, typeToken)
            } catch (e: JsonSyntaxException) {
                Log.e("YAMIN", "ChatRepositoryImpl:handleMessageResponse: ${e.message}", e)
                null
            }

        message?.let { botMessage ->
            if (botMessage.messages.isNotEmpty()) {
                botMessage.messages.forEach {
                    messages.onNext(ChatMessage.TextChatMessage(it, Sender.BOT))
                }
                botMessage.input?.let {
                    input.onNext(it)
                }
            }
        }
    }

    override fun closeRepository() {
        compositeDisposable.clear()
    }

}