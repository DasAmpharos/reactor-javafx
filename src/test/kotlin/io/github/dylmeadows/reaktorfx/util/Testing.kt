package io.github.dylmeadows.reaktorfx.util

import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.stubbing.OngoingStubbing
import org.mockito.stubbing.Stubber

fun <T> any(): T {
    Mockito.any<T>()
    return null as T
}

inline fun <reified T> mock(): T = Mockito.mock(T::class.java)

fun <T> Stubber.whenever(mock: T): T = `when`(mock)

fun <T> whenever(methodCall: T): OngoingStubbing<T> = `when`(methodCall)
