package io.github.dylmeadows.reaktorfx.source

import io.github.dylmeadows.reaktorfx.util.ChangeType
import io.github.dylmeadows.reaktorfx.util.ListChange
import io.github.dylmeadows.reaktorfx.observer.addDisposable
import javafx.application.Platform
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink

fun <T> ObservableList<T>.changesOf(): Flux<ListChange<T>> {
    return Flux.create { emitter ->
        val listener = onChanges(emitter)
        emitter.addDisposable(listener.asDisposable(this))
        addListener(listener)
    }
}

fun <T> ObservableList<T>.elementAdditions(): Flux<T> {
    return Flux.create { emitter ->
        val listener = onListAddition<T>(emitter)
        emitter.addDisposable(listener.asDisposable(this))
        addListener(listener)
    }
}

fun <T> ObservableList<T>.elementRemovals(): Flux<T> {
    return Flux.create { emitter ->
        val listener = onListRemoval(emitter)
        emitter.addDisposable(listener.asDisposable(this))
        addListener(listener)
    }
}

private fun <T> ListChangeListener<T>.asDisposable(observable: ObservableList<T>): Disposable {
    return Disposable { Platform.runLater { observable.removeListener(this) } }
}

private val <T> ListChangeListener.Change<T>.changeType: ChangeType
    get() {
        return when {
            wasAdded() -> ChangeType.ADDED
            wasRemoved() -> ChangeType.REMOVED
            else -> throw IllegalArgumentException("Unable to determine list change type")
        }
    }

private fun <T> onChanges(emitter: FluxSink<ListChange<T>>): ListChangeListener<T> {
    return ListChangeListener { change ->
        while (change.next()) {
            when (change.changeType) {
                ChangeType.ADDED ->
                    change.addedSubList.forEach {
                        emitter.next(ListChange(it, ChangeType.ADDED))
                    }
                ChangeType.REMOVED ->
                    change.removed.forEach {
                        emitter.next(ListChange(it, ChangeType.REMOVED))
                    }
            }
        }
    }
}

private fun <T> onListAddition(emitter: FluxSink<T>): ListChangeListener<T> {
    return ListChangeListener { change ->
        while (change.next()) {
            if (change.wasAdded()) {
                change.addedSubList.forEach { element ->
                    emitter.next(element)
                }
            }
        }
    }
}

private fun <T> onListRemoval(emitter: FluxSink<T>): ListChangeListener<T> {
    return ListChangeListener { change ->
        while (change.next()) {
            if (change.wasRemoved()) {
                change.removed.forEach { element ->
                    emitter.next(element)
                }
            }
        }
    }
}
