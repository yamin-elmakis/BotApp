package yamin.elmakis.bot.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import yamin.elmakis.domain.entity.InputType
import yamin.elmakis.domain.repo.ChatRepository
import yamin.elmakis.remotedatasource.RemoteDataImpl
import yamin.elmakis.repository.ChatRepositoryImpl
import yamin.elmakis.repository.data.source.RemoteData
import yamin.elmakis.repository.util.InputTypeCreator
import yamin.elmakis.repository.util.ParserHelper
import yamin.elmakis.repository.util.ParserHelperImpl

fun appDependencies() = Kodein.Module("AppDependencies") {

    bind<Gson>() with singleton {
        GsonBuilder()
            .registerTypeAdapter(InputType::class.java, InputTypeCreator())
            .create()
    }

    bind<ParserHelper>() with singleton {
        ParserHelperImpl(gson = instance())
    }

    bind<RemoteData>() with singleton {
        RemoteDataImpl(parser = instance())
    }

    bind<ChatRepository>() with singleton {
        ChatRepositoryImpl(remoteData = instance(), parser = instance())
    }
}