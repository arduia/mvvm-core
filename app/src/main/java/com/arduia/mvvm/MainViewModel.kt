package com.arduia.mvvm

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel(), LifecycleObserver{

    private val _hello = BaseLiveData<String>()
    val hello = _hello.asLiveData()


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){
        _hello post "Hello World from MVVM Core Libraries"
    }

}