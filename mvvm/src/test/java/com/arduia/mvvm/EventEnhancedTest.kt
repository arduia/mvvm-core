package com.arduia.mvvm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class EventEnhancedTest {

    @get:Rule
    val taskRule = InstantTaskExecutorRule()

    @Test
    fun `event factory function should create proper event`() {
        // Given
        val content = "test content"
        
        // When
        val event = event(content)
        
        // Then
        assertNotNull("Event should not be null", event)
        assertEquals("Event content should match", content, event.peekContent())
        assertFalse("Event should not be handled initially", event.hasHandled)
    }

    @Test
    fun `EventUnit should create Unit event`() {
        // Given & When
        val unitEvent = EventUnit
        
        // Then
        assertNotNull("EventUnit should not be null", unitEvent)
        assertEquals("EventUnit content should be Unit", Unit, unitEvent.peekContent())
        assertFalse("EventUnit should not be handled initially", unitEvent.hasHandled)
    }

    @Test
    fun `event with null content should work`() {
        // Given & When
        val nullEvent = event(null)
        
        // Then
        assertNotNull("Event object should not be null", nullEvent)
        assertNull("Event content should be null", nullEvent.peekContent())
        assertFalse("Event should not be handled initially", nullEvent.hasHandled)
    }

    @Test
    fun `event with complex object should work`() {
        // Given
        data class TestData(val name: String, val value: Int)
        val testData = TestData("test", 42)
        
        // When
        val event = event(testData)
        
        // Then
        assertNotNull("Event should not be null", event)
        assertEquals("Event content should match", testData, event.peekContent())
        assertFalse("Event should not be handled initially", event.hasHandled)
    }

    @Test
    fun `multiple peek calls should return same content`() {
        // Given
        val content = "test content"
        val event = event(content)
        
        // When & Then
        repeat(5) {
            assertEquals("Peek should always return same content", content, event.peekContent())
            assertFalse("Event should not be handled after peek", event.hasHandled)
        }
    }

    @Test
    fun `getContentIfNotHandled should consume event only once`() {
        // Given
        val content = "test content"
        val event = event(content)
        
        // When
        val firstCall = event.getContentIfNotHandled()
        val secondCall = event.getContentIfNotHandled()
        val thirdCall = event.getContentIfNotHandled()
        
        // Then
        assertEquals("First call should return content", content, firstCall)
        assertNull("Second call should return null", secondCall)
        assertNull("Third call should return null", thirdCall)
        assertTrue("Event should be handled after first call", event.hasHandled)
    }

    @Test
    fun `EventObserver should handle null events gracefully`() {
        // Given
        val lifecycleOwner = mock(LifecycleOwner::class.java)
        val lifecycle = LifecycleRegistry(lifecycleOwner)
        `when`(lifecycleOwner.lifecycle).thenReturn(lifecycle)

        val liveData = MutableLiveData<Event<String>>()
        var callbackInvoked = false
        
        val observer = EventObserver<String> { 
            callbackInvoked = true
        }
        
        // When
        liveData.observe(lifecycleOwner, observer)
        liveData.value = null
        
        // Then
        assertFalse("Callback should not be invoked for null event", callbackInvoked)
    }

    @Test
    fun `EventObserver should handle multiple events correctly`() {
        // Given
        val lifecycleOwner = mock(LifecycleOwner::class.java)
        val lifecycle = LifecycleRegistry(lifecycleOwner)
        `when`(lifecycleOwner.lifecycle).thenReturn(lifecycle)

        val liveData = MutableLiveData<Event<String>>()
        val receivedEvents = mutableListOf<String>()
        
        val observer = EventObserver<String> { content ->
            receivedEvents.add(content)
        }
        
        // When
        liveData.observe(lifecycleOwner, observer)
        liveData.value = event("event1")
        liveData.value = event("event2")
        liveData.value = event("event3")
        
        // Then
        assertEquals("Should receive all events", 3, receivedEvents.size)
        assertEquals("First event should be correct", "event1", receivedEvents[0])
        assertEquals("Second event should be correct", "event2", receivedEvents[1])
        assertEquals("Third event should be correct", "event3", receivedEvents[2])
    }

    @Test
    fun `EventObserver should not trigger for already handled events`() {
        // Given
        val lifecycleOwner = mock(LifecycleOwner::class.java)
        val lifecycle = LifecycleRegistry(lifecycleOwner)
        `when`(lifecycleOwner.lifecycle).thenReturn(lifecycle)

        val liveData = MutableLiveData<Event<String>>()
        val receivedEvents = mutableListOf<String>()
        
        val observer = EventObserver<String> { content ->
            receivedEvents.add(content)
        }
        
        val event = event("test")
        
        // When
        event.getContentIfNotHandled() // Handle the event manually
        liveData.observe(lifecycleOwner, observer)
        liveData.value = event
        
        // Then
        assertTrue("Event should be handled", event.hasHandled)
        assertEquals("Should not receive any events", 0, receivedEvents.size)
    }

    @Test
    fun `EventObserver should work with configuration changes`() {
        // Given
        val lifecycleOwner = mock(LifecycleOwner::class.java)
        val lifecycle = LifecycleRegistry(lifecycleOwner)
        `when`(lifecycleOwner.lifecycle).thenReturn(lifecycle)

        val liveData = MutableLiveData<Event<String>>()
        var eventCount = 0
        val eventValue = "config_change_test"
        
        val observer = EventObserver<String> { value ->
            assertEquals("Event content should match", eventValue, value)
            eventCount++
        }
        
        // When
        liveData.observe(lifecycleOwner, observer)
        liveData.value = event(eventValue)
        
        // Simulate configuration change
        lifecycle.makeFakeConfigurationChanges()
        
        // Then
        assertEquals("Event should be handled only once", 1, eventCount)
        assertTrue("Event should be handled", liveData.value?.hasHandled ?: false)
    }

    @Test
    fun `Event should maintain content integrity`() {
        // Given
        val originalContent = "original"
        val event = event(originalContent)
        
        // When
        val peekedContent = event.peekContent()
        val consumedContent = event.getContentIfNotHandled()
        val peekedAgain = event.peekContent()
        
        // Then
        assertEquals("Peeked content should match original", originalContent, peekedContent)
        assertEquals("Consumed content should match original", originalContent, consumedContent)
        assertEquals("Peeked again should still match original", originalContent, peekedAgain)
    }

    @Test
    fun `Event should be thread-safe for reading`() {
        // Given
        val content = "thread-safe-test"
        val event = event(content)
        
        // When & Then
        // Multiple threads peeking should all get the same content
        repeat(10) {
            Thread {
                assertEquals("Content should be consistent across threads", content, event.peekContent())
            }.start()
        }
        
        // Give threads time to complete
        Thread.sleep(100)
        
        assertFalse("Event should not be handled after just peeking", event.hasHandled)
    }
}