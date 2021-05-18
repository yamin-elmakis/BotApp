package yamin.elmakis.bot.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import yamin.elmakis.bot.ui.chat.ChatViewModel
import yamin.elmakis.domain.usecase.ChatUseCase

class BaseViewModelFactory(private val chatUseCase: ChatUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {

            ChatViewModel::class.java -> ChatViewModel(chatUseCase)

            else -> throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
        } as T
    }
}