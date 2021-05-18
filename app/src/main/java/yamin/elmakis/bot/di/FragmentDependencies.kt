package yamin.elmakis.bot.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import yamin.elmakis.bot.base.BaseViewModelFactory
import yamin.elmakis.bot.ui.chat.ChatContract
import yamin.elmakis.bot.ui.chat.ChatViewModel
import yamin.elmakis.domain.usecase.ChatUseCase
import yamin.elmakis.domain.usecase.ChatUseCaseImpl

fun fragmentDependencies(fragment: Fragment) = Kodein.Module("FragmentDependencies") {

    bind<ChatUseCase>() with provider {
        ChatUseCaseImpl(chatRepository = instance())
    }

    bind<BaseViewModelFactory>() with provider {
        BaseViewModelFactory(useCase = instance())
    }

    bind<ChatContract.ChatViewModel>() with provider {
        val factory: BaseViewModelFactory = instance()
        fragment.viewModels<ChatViewModel>(factoryProducer = { factory }).value
    }

}