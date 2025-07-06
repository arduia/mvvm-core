package com.arduia.mvvm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class LiveDataExtensionsTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `infix set operator should work on MutableLiveData`() {
        // Given
        val liveData = MutableLiveData<String>()
        val testValue = "test value"
        
        // When
        liveData set testValue
        
        // Then
        val result = liveData.getOrAwaitValue()
        assertEquals("Infix set should set the value", testValue, result)
    }

    @Test
    fun `infix post operator should work on MutableLiveData`() {
        // Given
        val liveData = MutableLiveData<String>()
        val testValue = "test value"
        
        // When
        liveData post testValue
        
        // Then
        val result = liveData.getOrAwaitValue()
        assertEquals("Infix post should post the value", testValue, result)
    }

    @Test
    fun `infix set operator should work with null values`() {
        // Given
        val liveData = MutableLiveData<String?>()
        liveData.value = "initial"
        
        // When
        liveData set null
        
        // Then
        val result = liveData.getOrAwaitValue()
        assertNull("Infix set should set null value", result)
    }

    @Test
    fun `infix post operator should work with null values`() {
        // Given
        val liveData = MutableLiveData<String?>()
        liveData.value = "initial"
        
        // When
        liveData post null
        
        // Then
        val result = liveData.getOrAwaitValue()
        assertNull("Infix post should post null value", result)
    }

    @Test
    fun `infix operators should work with different data types`() {
        // Given
        val intLiveData = MutableLiveData<Int>()
        val boolLiveData = MutableLiveData<Boolean>()
        val listLiveData = MutableLiveData<List<String>>()
        
        val intValue = 42
        val boolValue = true
        val listValue = listOf("a", "b", "c")
        
        // When
        intLiveData set intValue
        boolLiveData post boolValue
        listLiveData set listValue
        
        // Then
        assertEquals("Int infix set should work", intValue, intLiveData.getOrAwaitValue())
        assertEquals("Boolean infix post should work", boolValue, boolLiveData.getOrAwaitValue())
        assertEquals("List infix set should work", listValue, listLiveData.getOrAwaitValue())
    }

    @Test
    fun `BaseLiveData should work with different generic types`() {
        // Given & When
        val stringLiveData = BaseLiveData<String>()
        val intLiveData = BaseLiveData<Int>()
        val listLiveData = BaseLiveData<List<String>>()
        
        // Then
        assertNotNull("String BaseLiveData should not be null", stringLiveData)
        assertNotNull("Int BaseLiveData should not be null", intLiveData)
        assertNotNull("List BaseLiveData should not be null", listLiveData)
    }

    @Test
    fun `BaseLiveData asLiveData should return readonly LiveData`() {
        // Given
        val baseLiveData = BaseLiveData<String>()
        
        // When
        val liveData = baseLiveData.asLiveData()
        
        // Then
        assertNotNull("asLiveData() should return non-null LiveData", liveData)
        assertSame("asLiveData() should return the same instance", baseLiveData, liveData)
    }

    @Test
    fun `BaseLiveData with init value should be immediately available`() {
        // Given
        val initValue = "initial value"
        
        // When
        val baseLiveData = BaseLiveData(initValue)
        
        // Then
        val result = baseLiveData.getOrAwaitValue()
        assertEquals("Init value should be immediately available", initValue, result)
    }

    @Test
    fun `BaseLiveData with null init value should have null value`() {
        // Given & When
        val baseLiveData = BaseLiveData<String>(null)
        
        // Then
        val result = baseLiveData.getOrAwaitValue()
        assertNull("Null init value should result in null", result)
    }

    @Test
    fun `BaseLiveData without init value should have null value`() {
        // Given & When
        val baseLiveData = BaseLiveData<String>()
        
        // Then
        val result = baseLiveData.getOrAwaitValue()
        assertNull("No init value should result in null", result)
    }

    @Test
    fun `EventLiveData typealias should work correctly`() {
        // Given & When
        val eventLiveData = EventLiveData<String>()
        val testEvent = Event("test")
        
        // When
        eventLiveData.value = testEvent
        
        // Then
        val result = eventLiveData.getOrAwaitValue()
        assertNotNull("EventLiveData should work", result)
        assertEquals("Event content should match", "test", result.peekContent())
    }

    @Test
    fun `EventLiveData with init value should work`() {
        // Given
        val initEvent = Event("initial")
        
        // When
        val eventLiveData = EventLiveData<String>(initEvent)
        
        // Then
        val result = eventLiveData.getOrAwaitValue()
        assertNotNull("EventLiveData with init should work", result)
        assertEquals("Event content should match", "initial", result.peekContent())
    }

    @Test
    fun `infix operators should work with BaseLiveData`() {
        // Given
        val baseLiveData = BaseLiveData<String>()
        val setValue = "set value"
        val postValue = "post value"
        
        // When
        baseLiveData set setValue
        var result = baseLiveData.getOrAwaitValue()
        assertEquals("Infix set should work with BaseLiveData", setValue, result)
        
        baseLiveData post postValue
        result = baseLiveData.getOrAwaitValue()
        assertEquals("Infix post should work with BaseLiveData", postValue, result)
    }

    @Test
    fun `infix operators should work with EventLiveData`() {
        // Given
        val eventLiveData = EventLiveData<String>()
        val event1 = Event("event1")
        val event2 = Event("event2")
        
        // When
        eventLiveData set event1
        var result = eventLiveData.getOrAwaitValue()
        assertEquals("Infix set should work with EventLiveData", "event1", result.peekContent())
        
        eventLiveData post event2
        result = eventLiveData.getOrAwaitValue()
        assertEquals("Infix post should work with EventLiveData", "event2", result.peekContent())
    }

    @Test
    fun `complex nested generic types should work`() {
        // Given
        val complexLiveData = BaseLiveData<Map<String, List<Int>>>()
        val complexValue = mapOf(
            "key1" to listOf(1, 2, 3),
            "key2" to listOf(4, 5, 6)
        )
        
        // When
        complexLiveData set complexValue
        
        // Then
        val result = complexLiveData.getOrAwaitValue()
        assertEquals("Complex nested types should work", complexValue, result)
    }

    @Test
    fun `data class with LiveData should work`() {
        // Given
        data class TestData(val name: String, val age: Int)
        val testData = TestData("John", 25)
        val liveData = BaseLiveData<TestData>()
        
        // When
        liveData set testData
        
        // Then
        val result = liveData.getOrAwaitValue()
        assertEquals("Data class should work", testData, result)
        assertEquals("Data class properties should be preserved", "John", result.name)
        assertEquals("Data class properties should be preserved", 25, result.age)
    }

    @Test
    fun `multiple consecutive operations should work`() {
        // Given
        val liveData = BaseLiveData<String>()
        
        // When
        liveData set "first"
        val first = liveData.getOrAwaitValue()
        
        liveData post "second"
        val second = liveData.getOrAwaitValue()
        
        liveData set "third"
        val third = liveData.getOrAwaitValue()
        
        // Then
        assertEquals("First operation should work", "first", first)
        assertEquals("Second operation should work", "second", second)
        assertEquals("Third operation should work", "third", third)
    }
}