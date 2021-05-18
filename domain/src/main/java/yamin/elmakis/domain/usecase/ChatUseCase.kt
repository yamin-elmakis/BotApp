package yamin.elmakis.domain.usecase

import io.reactivex.Observable
import yamin.elmakis.domain.entity.ChatMessage
import yamin.elmakis.domain.entity.InputType
import yamin.elmakis.domain.entity.UserAnswer
import yamin.elmakis.domain.repo.ChatRepository

interface ChatUseCase {
    fun getChatStream(): Observable<ChatMessage>
    fun getInputStream(): Observable<InputType>

    fun sendUserAnswer(userAnswer: UserAnswer)
}

class ChatUseCaseImpl(private val chatRepository: ChatRepository) : ChatUseCase {

    override fun getChatStream(): Observable<ChatMessage> {
        return chatRepository.getChatStream()
    }

    override fun getInputStream(): Observable<InputType> {
        return chatRepository.getInputStream()
    }

    override fun sendUserAnswer(userAnswer: UserAnswer) {
        chatRepository.sendUserAnswer(userAnswer)
    }
}