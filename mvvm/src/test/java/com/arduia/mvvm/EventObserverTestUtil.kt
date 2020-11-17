package com.arduia.mvvm

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry

fun LifecycleRegistry.makeFakeConfigurationChanges() {
    handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    handleLifecycleEvent(Lifecycle.Event.ON_START)
    handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
}