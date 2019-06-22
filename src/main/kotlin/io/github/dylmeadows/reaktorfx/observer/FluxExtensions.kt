package io.github.dylmeadows.reaktorfx.observer

import io.github.dylmeadows.reaktorfx.util.ErrorHandler
import io.github.dylmeadows.reaktorfx.util.ErrorHandlers
import javafx.beans.binding.Binding
import javafx.beans.value.WritableDoubleValue
import javafx.beans.value.WritableFloatValue
import javafx.beans.value.WritableIntegerValue
import javafx.beans.value.WritableLongValue
import javafx.beans.value.WritableValue
import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink

fun <T> Flux<T>.asBinding(onError: ErrorHandler = ErrorHandlers.doNothing): Binding<T> =
    PublisherBinding(this, onError)

fun <T> Flux<T>.feedTo(observable: WritableValue<T>, onError: ErrorHandler = ErrorHandlers.doNothing): Disposable =
    subscribe(observable::setValue, onError)

fun Flux<Long>.feedTo(observable: WritableLongValue, onError: ErrorHandler = ErrorHandlers.doNothing): Disposable =
    subscribe(observable::setValue, onError)

fun Flux<Int>.feedTo(observable: WritableIntegerValue, onError: ErrorHandler = ErrorHandlers.doNothing): Disposable =
    subscribe(observable::setValue, onError)

fun Flux<Float>.feedTo(observable: WritableFloatValue, onError: ErrorHandler = ErrorHandlers.doNothing): Disposable =
    subscribe(observable::setValue, onError)


fun Flux<Double>.feedTo(observable: WritableDoubleValue, onError: ErrorHandler = ErrorHandlers.doNothing): Disposable =
    subscribe(observable::setValue, onError)

fun <T> Flux<T>.feedTo(sink: FluxSink<T>): Disposable =
    subscribe({ sink.next(it) }, sink::error, sink::complete)
