package yamin.elmakis.bot.util

import androidx.lifecycle.LiveData

open class SafeLiveData<T>(defaultValue: T, private val callback: Callback? = null) : LiveData<T>() {

    interface Callback {
        fun onActive()
        fun onInactive()
    }

    init {
        value = defaultValue
    }

    override fun getValue(): T {
        return super.getValue()!!
    }

    override fun onActive() {
        callback?.onActive()
    }

    override fun onInactive() {
        callback?.onInactive()
    }
}

class MutableSafeLiveData<T>(defaultValue: T, callback: Callback? = null) :
    SafeLiveData<T>(defaultValue, callback) {

    public override fun setValue(value: T) {
        super.setValue(value)
    }

    public override fun postValue(value: T) {
        super.postValue(value)
    }
}
