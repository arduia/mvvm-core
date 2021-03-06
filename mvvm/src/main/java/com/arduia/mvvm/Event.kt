/*

    Copyright (C) 2020  Aung Ye Htet

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.arduia.mvvm

import androidx.lifecycle.Observer

/**
 * Inspired by Google
 * Event State with data that can be consumed once.
 */
open class Event<out T> (private val content: T){

    var hasHandled = false
        private set

    fun getContentIfNotHandled(): T?{
        return when (hasHandled){
            true -> null
            false -> {
                hasHandled = true
                content
            }
        }
    }
    fun peekContent(): T = content
}

/**
 * Observer that consume {@link Event} Data
 */
class EventObserver<T> (private val onEventUnhandledContent: (T) -> Unit): Observer<Event<T>>{

    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}

/**
 * Return New Unit Event for State Representation.
 */
val EventUnit get() =  Event(Unit)

/**
 * New Event with content{T}
 */
fun <T>event(content: T) = Event(content)