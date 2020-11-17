package com.arduia.mvvm

import org.hamcrest.core.Is.`is`
import org.junit.Assert.*
import org.junit.Test

class EventTest{

    @Test
    fun shouldWorkInitAndPeekContent(){
        val eventValue = "EventValue"
        val event = Event(eventValue)

        assertFalse(event.hasHandled)
        assertThat(event.peekContent(), `is`(eventValue))
        assertThat(event.peekContent(), `is`(eventValue))
        assertFalse(event.hasHandled)
    }

    @Test
    fun shouldWorkContentIfNotHandled(){
        val eventValue = "EventValue"
        val event = Event(eventValue)

        assertThat(event.getContentIfNotHandled(),`is`(eventValue))
        assertTrue(event.hasHandled)
        assertNull(event.getContentIfNotHandled())
    }
}