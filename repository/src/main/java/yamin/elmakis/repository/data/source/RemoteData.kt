package yamin.elmakis.repository.data.source

import io.reactivex.Single

interface RemoteData {
    fun sendUserAnswer(userAnswer: String): Single<String>
}