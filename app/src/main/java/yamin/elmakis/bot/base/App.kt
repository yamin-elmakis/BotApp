package yamin.elmakis.bot.base

import android.app.Application
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import yamin.elmakis.bot.di.appDependencies
import yamin.elmakis.domain.repo.ChatRepository

class App : Application(), KodeinAware {

    private val repository: ChatRepository by instance()

    override val kodein = Kodein.lazy {
        import(appDependencies())
    }

    override fun onTerminate() {
        repository.closeRepository()
        super.onTerminate()
    }
}