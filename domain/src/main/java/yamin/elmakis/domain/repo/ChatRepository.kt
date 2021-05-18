package yamin.elmakis.domain.repo

import io.reactivex.Observable
import io.reactivex.Single
import yamin.elmakis.domain.entity.ChatMessage
import yamin.elmakis.domain.entity.InputType
import yamin.elmakis.domain.entity.UserAnswer

interface ChatRepository {
    fun getChatStream() : Observable<ChatMessage>
    fun getInputStream(): Observable<InputType>

    fun sendUserAnswer(userAnswer: UserAnswer)

    fun closeRepository()
}