package io.github.dylmeadows.reaktorfx.observer

import io.github.dylmeadows.reaktorfx.util.ErrorHandler
import io.github.dylmeadows.reaktorfx.util.any
import io.github.dylmeadows.reaktorfx.util.mock
import io.github.dylmeadows.reaktorfx.util.whenever
import javafx.beans.value.WritableDoubleValue
import javafx.beans.value.WritableFloatValue
import javafx.beans.value.WritableIntegerValue
import javafx.beans.value.WritableLongValue
import javafx.beans.value.WritableValue
import org.junit.Assert.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import reactor.core.Disposable
import reactor.core.publisher.DirectProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink

class FluxTest {

    @Test
    fun `asBinding returns a new PublisherBinding`() {
        val flux = mock<Flux<Any>>()
        val disposable = mock<Disposable>()
        whenever(flux.subscribe(any(), any())).thenReturn(disposable)

        assertNotNull(flux.asBinding())
    }

    @Test
    fun `feedTo with WritableValue invokes WritableValue#setValue when a value is emitted`() {
        val value = mock<WritableValue<Any>>()
        val flux = DirectProcessor.create<Any>()

        flux.feedTo(value)
        flux.onNext("abc")

        verify(value, times(1))
            .value = eq("abc")
    }

    @Test
    fun `feedTo with WritableValue invokes onError when an error is emitted`() {
        val value = mock<WritableValue<Any>>()
        val onError = mock<ErrorHandler>()
        val flux = DirectProcessor.create<Any>()
        whenever(onError.invoke(any())).thenReturn(Unit)

        flux.feedTo(value, onError)
        flux.onError(Exception())

        verify(onError, times(1))
            .invoke(any())
    }

    @Test
    fun `feedTo with WritableLongProperty invokes WritableLongProperty#setValue when a value is emitted`() {
        val value = mock<WritableLongValue>()
        val flux = DirectProcessor.create<Long>()

        flux.feedTo(value)
        flux.onNext(1L)

        verify(value, times(1))
            .value = eq(1L)
    }

    @Test
    fun `feedTo with WritableLongProperty invokes onError when an error is emitted`() {
        val value = mock<WritableLongValue>()
        val onError = mock<ErrorHandler>()
        val flux = DirectProcessor.create<Long>()
        whenever(onError.invoke(any())).thenReturn(Unit)

        flux.feedTo(value, onError)
        flux.onError(Exception())

        verify(onError, times(1))
            .invoke(any())
    }

    @Test
    fun `feedTo with WritableIntegerProperty invokes WritableIntegerProperty#setValue when a value is emitted`() {
        val value = mock<WritableIntegerValue>()
        val flux = DirectProcessor.create<Int>()

        flux.feedTo(value)
        flux.onNext(1)

        verify(value, times(1))
            .value = eq(1)
    }

    @Test
    fun `feedTo with WritableIntegerProperty invokes onError when an error is emitted`() {
        val value = mock<WritableIntegerValue>()
        val onError = mock<ErrorHandler>()
        val flux = DirectProcessor.create<Int>()
        whenever(onError.invoke(any())).thenReturn(Unit)

        flux.feedTo(value, onError)
        flux.onError(Exception())

        verify(onError, times(1))
            .invoke(any())
    }

    @Test
    fun `feedTo with WritableFloatProperty invokes WritableFloatProperty#setValue when a value is emitted`() {
        val value = mock<WritableFloatValue>()
        val flux = DirectProcessor.create<Float>()

        flux.feedTo(value)
        flux.onNext(1f)

        verify(value, times(1))
            .value = eq(1f)
    }

    @Test
    fun `feedTo with WritableFloatProperty invokes onError when an error is emitted`() {
        val value = mock<WritableFloatValue>()
        val onError = mock<ErrorHandler>()
        val flux = DirectProcessor.create<Float>()
        whenever(onError.invoke(any())).thenReturn(Unit)

        flux.feedTo(value, onError)
        flux.onError(Exception())

        verify(onError, times(1))
            .invoke(any())
    }

    @Test
    fun `feedTo with WritableDoubleProperty invokes WritableDoubleProperty#setValue when a value is emitted`() {
        val value = mock<WritableDoubleValue>()
        val flux = DirectProcessor.create<Double>()

        flux.feedTo(value)
        flux.onNext(1.0)

        verify(value, times(1))
            .value = eq(1.0)
    }

    @Test
    fun `feedTo with WritableDoubleProperty invokes onError when an error is emitted`() {
        val value = mock<WritableDoubleValue>()
        val onError = mock<ErrorHandler>()
        val flux = DirectProcessor.create<Double>()
        whenever(onError.invoke(any())).thenReturn(Unit)

        flux.feedTo(value, onError)
        flux.onError(Exception())

        verify(onError, times(1))
            .invoke(any())
    }

    @Test
    fun `feedTo with MonoSink invokes MonoSink#success with value onNext`() {
        val sink = mock<FluxSink<Any>>()
        val flux = DirectProcessor.create<Any>()

        flux.feedTo(sink)
        flux.onNext("abc")

        verify(sink, times(1))
            .next(eq("abc"))
    }

    @Test
    fun `feedTo with MonoSink invokes MonoSink#success with no value onComplete`() {
        val sink = mock<FluxSink<Any>>()
        val flux = DirectProcessor.create<Any>()

        flux.feedTo(sink)
        flux.onComplete()

        verify(sink, times(1))
            .complete()
    }

    @Test
    fun `feedTo with MonoSink invokes MonoSink#error onError`() {
        val sink = mock<FluxSink<Any>>()
        val flux = DirectProcessor.create<Any>()

        flux.feedTo(sink)
        flux.onError(Exception())

        verify(sink, times(1))
            .error(any())
    }
}