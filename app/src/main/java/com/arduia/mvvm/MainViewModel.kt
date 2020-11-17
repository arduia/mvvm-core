package com.arduia.mvvm

import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel(){

    private val _isAvailable = BaseLiveData(initValue = true)
    val isAvailable get() = _isAvailable.asLiveData()

    private val _onRotate = EventLiveData<Unit>()
    val onRotate get() = _onRotate.asLiveData()

    private val _onToastShow = EventLiveData<String>()
    val onToastShow get() = _onToastShow.asLiveData()

    fun setAvailableOff(){
        _isAvailable set false
    }

    fun setAvailableOn(){
        _isAvailable set true
    }

    fun rotate(){
        _onRotate set EventUnit
    }

    fun showMessage(message: String){
        _onToastShow set event(message)
    }
}