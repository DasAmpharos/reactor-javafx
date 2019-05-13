package io.github.dylmeadows.reaktorfx.source

import io.github.dylmeadows.reaktorfx.util.Change
import io.github.dylmeadows.reaktorfx.util.addDisposable
import io.github.dylmeadows.reaktorfx.util.asComposite
import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import reactor.core.Disposable
import reactor.core.publisher.Flux
import java.util.*
import java.util.stream.Stream

fun <T> ObservableValue<T>.valuesOf(): Flux<T> {
    return Flux.create { emitter ->
        if (value != null) emitter.next(value)
        val listener: ChangeListener<T> = ChangeListener { _, _, newValue ->
            if (newValue != null) emitter.next(newValue)
        }
        emitter.addDisposable(listener.asDisposable(this))
        addListener(listener)
    }
}

fun <T> ObservableValue<T>.nullableValuesOf(): Flux<Optional<T>> {
    return Flux.create { emitter ->
        emitter.next(Optional.ofNullable(value))
        val listener: ChangeListener<T> = ChangeListener { _, _, newValue ->
            emitter.next(Optional.ofNullable(newValue))
        }
        emitter.addDisposable(listener.asDisposable(this))
        addListener(listener)
    }
}

fun <T> ObservableValue<T>.changesOf(): Flux<Change<T>> {
    return Flux.create { emitter ->
        val listener: ChangeListener<T> = ChangeListener { _, oldValue, newValue ->
            emitter.next(Change(oldValue, newValue))
        }
        emitter.addDisposable(listener.asDisposable(this))
        addListener(listener)
    }
}

fun <T> ObservableValue<T>.invalidationsOf(): Flux<Observable> {
    return Flux.create { emitter ->
        val listener = InvalidationListener { emitter.next(it) }
        emitter.addDisposable(listener.asDisposable(this))
        addListener(listener)
    }
}

fun <T1, T2, R> ObservableValue<T1>.combineWith(
    p2: ObservableValue<T2>,
    combinator: (T1?, T2?) -> R
): Flux<R> {
    return Flux.create<R> { emitter ->
        val emit: () -> Unit = { emitter.next(combinator(value, p2.value)) }
        val listener = InvalidationListener { emit() }
        emitter.addDisposable(Stream.of(this, p2)
            .peek { it.addListener(listener) }
            .map { listener.asDisposable(it) }
            .asComposite())
        emit()
    }
}

fun <T1, T2, T3, R> ObservableValue<T1>.combineWith(
    p2: ObservableValue<T2>,
    p3: ObservableValue<T3>,
    combinator: (T1?, T2?, T3?) -> R
): Flux<R> {
    return Flux.create { emitter ->
        val emit: () -> Unit = { emitter.next(combinator(value, p2.value, p3.value)) }
        val listener = InvalidationListener { emit() }
        emitter.addDisposable(Stream.of(this, p2, p3)
            .peek { it.addListener(listener) }
            .map { listener.asDisposable(it) }
            .asComposite())
        emit()
    }
}

fun <T1, T2, T3, T4, R> ObservableValue<T1>.combineWith(
    p2: ObservableValue<T2>,
    p3: ObservableValue<T3>,
    p4: ObservableValue<T4>,
    combinator: (T1?, T2?, T3?, T4?) -> R
): Flux<R> {
    return Flux.create { emitter ->
        val emit: () -> Unit = { emitter.next(combinator(value, p2.value, p3.value, p4.value)) }
        val listener = InvalidationListener { emit() }
        emitter.addDisposable(Stream.of(this, p2, p3, p4)
            .peek { it.addListener(listener) }
            .map { listener.asDisposable(it) }
            .asComposite())
        emit()
    }
}

private fun <T> ChangeListener<T>.asDisposable(observable: ObservableValue<T>): Disposable {
    return Disposable { Platform.runLater { observable.removeListener(this) } }
}

private fun <T> InvalidationListener.asDisposable(observable: ObservableValue<T>): Disposable {
    return Disposable { Platform.runLater { observable.removeListener(this) } }
}