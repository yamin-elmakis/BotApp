package yamin.elmakis.remotedatasource

import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import yamin.elmakis.domain.entity.AnswerType
import yamin.elmakis.domain.entity.InputType
import yamin.elmakis.domain.entity.SelectionOption
import yamin.elmakis.domain.entity.UserAnswer
import yamin.elmakis.repository.data.response.BotResponse
import yamin.elmakis.repository.data.source.RemoteData
import yamin.elmakis.repository.util.ParserHelper
import java.util.*
import java.util.concurrent.TimeUnit

class RemoteDataImpl(private val parser: ParserHelper) : RemoteData {

    companion object {
        const val WAIT_TIME_MILLIS: Long = 2000
    }

    override fun sendUserAnswer(userAnswer: String): Single<String> {
        return Single.create<String> { emitter ->
            val answer: UserAnswer = try {
                val typeToken = TypeToken.get(UserAnswer::class.java).type
                parser.fromJson<UserAnswer>(userAnswer, typeToken)!!
            } catch (e: Exception) {
                emitter.onError(e)
                return@create
            }
            val response: BotResponse = when (answer.type) {
                AnswerType.TEXT -> getStep2(answer.answer)
                AnswerType.NUMBER -> getStep3()
                AnswerType.SELECTION -> {
                    val selected: SelectionOption = try {
                        val typeToken = TypeToken.get(SelectionOption::class.java).type
                        parser.fromJson<SelectionOption>(answer.answer, typeToken)!!
                    } catch (e: Exception) {
                        emitter.onError(e)
                        return@create
                    }
                    when (selected) {
                        SelectionOption.YES -> getStep4()
                        SelectionOption.NO -> getStep5()
                        SelectionOption.RESTART -> getRestart()
                        SelectionOption.EXIT -> getStep5()
                        else -> {
                            emitter.onError(InputMismatchException())
                            return@create
                        }
                    }
                }
            }
            val responseStr = parser.toJson(response)
            emitter.onSuccess(responseStr)
        }.delay(WAIT_TIME_MILLIS, TimeUnit.MILLISECONDS)
    }

    private fun getStep2(name: String): BotResponse {
        val messages = listOf("Nice to meet you $name :)", "What is your phone number?")
        return BotResponse(messages, InputType.Number)
    }

    private fun getStep3(): BotResponse {
        val messages = listOf("Do you agree to our terms of service?")
        val selectionList = listOf(SelectionOption.YES, SelectionOption.NO)
        return BotResponse(messages, InputType.Selection(selectionList))
    }

    private fun getStep4(): BotResponse {
        val messages = listOf("Thanks!", "this is the last step!", "what do you want to do now?")
        val selectionList = listOf(SelectionOption.RESTART, SelectionOption.EXIT)
        return BotResponse(messages, InputType.Selection(selectionList))
    }

    private fun getStep5(): BotResponse {
        val messages = listOf("Bye Bye!")
        return BotResponse(messages, InputType.None)
    }

    private fun getRestart(): BotResponse {
        return BotResponse(listOf<String>(), null)
    }
}