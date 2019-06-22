package io.github.dylmeadows.reaktorfx.source

import io.github.dylmeadows.reaktorfx.util.ChangeType
import io.github.dylmeadows.reaktorfx.util.KeyValuePair
import io.github.dylmeadows.reaktorfx.util.MapChange
import io.github.dylmeadows.reaktorfx.observer.addDisposable
import javafx.application.Platform
import javafx.collections.MapChangeListener
import javafx.collections.ObservableMap
import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink

fun <K, V> ObservableMap<K, V>.changesOf(): Flux<MapChange<K, V>> {
    return Flux.create { emitter ->
        val listener = onChanges(emitter)
        emitter.addDisposable(listener.asDisposable(this))
        addListener(listener)
    }
}

fun <K, V> ObservableMap<K, V>.entryAdditions(): Flux<KeyValuePair<K, V>> {
    return Flux.create { emitter ->
        val listener = onEntryAddition(emitter)
        emitter.addDisposable(listener.asDisposable(this))
        addListener(listener)
    }
}

fun <K, V> ObservableMap<K, V>.entryRemovals(): Flux<KeyValuePair<K, V>> {
    return Flux.create { emitter ->
        val listener = onEntryRemoval(emitter)
        emitter.addDisposable(listener.asDisposable(this))
        addListener(listener)
    }
}

private fun <K, V> MapChangeListener<K, V>.asDisposable(observable: ObservableMap<K, V>): Disposable {
    return Disposable { Platform.runLater { observable.removeListener(this) } }
}

private fun <K, V> onChanges(emitter: FluxSink<MapChange<K, V>>): MapChangeListener<K, V> {
    return MapChangeListener { change ->
        when {
            change.wasAdded() ->
                emitter.next(
                    MapChange(
                        change.key, change.valueAdded,
                        ChangeType.ADDED
                    )
                )
            change.wasRemoved() ->
                emitter.next(
                    MapChange(
                        change.key, change.valueRemoved,
                        ChangeType.REMOVED
                    )
                )
        }
    }
}

private fun <K, V> onEntryAddition(emitter: FluxSink<KeyValuePair<K, V>>): MapChangeListener<K, V> {
    return MapChangeListener { change ->
        if (change.wasAdded()) {
            emitter.next(KeyValuePair(change.key, change.valueAdded))
        }
    }
}

private fun <K, V> onEntryRemoval(emitter: FluxSink<KeyValuePair<K, V>>): MapChangeListener<K, V> {
    return MapChangeListener { change ->
        if (change.wasRemoved()) {
            emitter.next(KeyValuePair(change.key, change.valueRemoved))
        }
    }
}
