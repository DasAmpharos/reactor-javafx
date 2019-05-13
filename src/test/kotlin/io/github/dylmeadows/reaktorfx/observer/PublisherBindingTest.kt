package io.github.dylmeadows.reaktorfx.observer

import io.github.dylmeadows.reaktorfx.util.ErrorHandler
import io.github.dylmeadows.reaktorfx.util.any
import io.github.dylmeadows.reaktorfx.util.mock
import io.github.dylmeadows.reaktorfx.util.whenever
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import reactor.core.publisher.DirectProcessor

class PublisherBindingTest {

    private lateinit var flux: DirectProcessor<Any>
    private lateinit var onError: ErrorHandler
    private lateinit var testSubject: PublisherBinding<Any>

    @BeforeEach
    fun setup() {
        onError = mock()
        flux = DirectProcessor.create()
        testSubject = PublisherBinding(flux, onError)
    }

    @Test
    fun `value updates when flux emits value`() {
        assertNull(testSubject.value)

        flux.onNext(100)
        assertEquals(100, testSubject.value)

        flux.onNext(200)
        assertEquals(200, testSubject.value)

        flux.onNext(300)
        assertEquals(300, testSubject.value)
    }

    @Test
    fun `binding is invalidated when flux emits a value and value is not accessed`() {
        flux.onNext(100)

        assertFalse(testSubject.isValid)
    }

    @Test
    fun `binding is revalidated when flux emits a value and get is invoked`() {
        flux.onNext(100)
        testSubject.get()

        assertTrue(testSubject.isValid)
    }

    @Test
    fun `isDisposed returns true when dispose is invoked`() {
        testSubject.dispose()

        assertTrue(testSubject.isDisposed)
    }

    @Test
    fun `isDisposed returns true when flux emits complete`() {
        flux.onComplete()

        assertTrue(testSubject.isDisposed)
    }

    @Test
    fun `isDisposed returns true when flux emits error`() {
        whenever(onError.invoke(any())).thenReturn(Unit)

        flux.onError(Exception())

        assertTrue(testSubject.isDisposed)
        verify(onError, times(1))
            .invoke(any())
    }
}
