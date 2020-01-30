package co.kyald.coronavirustracking.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class LiveDataObserver<T>(private val liveData: MutableLiveData<T>) : Observer<T> {
    override fun onChanged(t: T?) {
        liveData.value = t
    }
}