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
import reactor.core.publisher.Mono
import reactor.core.publisher.MonoSink

fun <T> Mono<T>.asBinding(onError: ErrorHandler = ErrorHandlers.doNothing): Binding<T> =
    PublisherBinding(this, onError)

fun <T> Mono<T>.feedTo(observable: WritableValue<T>, onError: ErrorHandler = ErrorHandlers.doNothing): Disposable =
    subscribe(observable::setValue, onError)

fun Mono<Long>.feedTo(observable: WritableLongValue, onError: ErrorHandler = ErrorHandlers.doNothing): Disposable =
    subscribe(observable::setValue, onError)

fun Mono<Int>.feedTo(observable: WritableIntegerValue, onError: ErrorHandler = ErrorHandlers.doNothing): Disposable =
    subscribe(observable::setValue, onError)

fun Mono<Float>.feedTo(observable: WritableFloatValue, onError: ErrorHandler = ErrorHandlers.doNothing): Disposable =
    subscribe(observable::setValue, onError)

fun Mono<Double>.feedTo(observable: WritableDoubleValue, onError: ErrorHandler = ErrorHandlers.doNothing): Disposable =
    subscribe(observable::setValue, onError)

fun <T> Mono<T>.feedTo(sink: MonoSink<T>): Disposable =
    subscribe({ sink.success(it) }, sink::error, sink::success)
