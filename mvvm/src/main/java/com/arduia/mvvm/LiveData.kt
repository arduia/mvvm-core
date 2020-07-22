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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class BaseLiveData<T> : MutableLiveData<T>(){
    fun asLiveData(): LiveData<T> = this
}

typealias EventLiveData<T> = BaseLiveData<Event<T>>

infix fun <T> MutableLiveData<T>.set(value: T) {
    setValue(value)
}

infix fun <T> MutableLiveData<T>.post(value: T){
    postValue(value)
}