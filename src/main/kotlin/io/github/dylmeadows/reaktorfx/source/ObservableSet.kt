package io.github.dylmeadows.reaktorfx.source

import io.github.dylmeadows.reaktorfx.util.ChangeType
import io.github.dylmeadows.reaktorfx.util.SetChange
import io.github.dylmeadows.reaktorfx.util.addDisposable
import javafx.application.Platform
import javafx.collections.ObservableSet
import javafx.collections.SetChangeListener
import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink

fun <T> ObservableSet<T>.changesOf(): Flux<SetChange<T>> {
    return Flux.create { emitter ->
        val listener = onChanges(emitter)
        emitter.addDisposable(listener.asDisposable(this))
        addListener(listener)
    }
}

fun <T> ObservableSet<T>.elementAdditions(): Flux<T> {
    return Flux.create { emitter ->
        val listener = onSetAddition(emitter)
        emitter.addDisposable(listener.asDisposable(this))
        addListener(listener)
    }
}

fun <T> ObservableSet<T>.elementRemovals(): Flux<T> {
    return Flux.create { emitter ->
        val listener = onSetRemoval(emitter)
        emitter.addDisposable(listener.asDisposable(this))
        addListener(listener)
    }
}

private fun <T> SetChangeListener<T>.asDisposable(observable: ObservableSet<T>): Disposable {
    return Disposable { Platform.runLater { observable.removeListener(this) } }
}

private fun <T> onChanges(emitter: FluxSink<SetChange<T>>): SetChangeListener<T> {
    return SetChangeListener { change ->
        if (change.wasAdded())
            emitter.next(SetChange(change.elementAdded, ChangeType.ADDED))
        if (change.wasRemoved())
            emitter.next(SetChange(change.elementRemoved, ChangeType.REMOVED))
    }
}

private fun <T> onSetAddition(emitter: FluxSink<T>): SetChangeListener<T> {
    return SetChangeListener { change ->
        if (change.wasAdded())
            emitter.next(change.elementAdded)
    }
}

private fun <T> onSetRemoval(emitter: FluxSink<T>): SetChangeListener<T> {
    return SetChangeListener { change ->
        if (change.wasRemoved())
            emitter.next(change.elementRemoved)
    }
}