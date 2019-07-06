package io.github.dylmeadows.reaktorfx.source

import io.github.dylmeadows.reaktorfx.observer.addDisposable
import io.github.dylmeadows.reaktorfx.util.ChangeType
import io.github.dylmeadows.reaktorfx.util.MapChange
import javafx.application.Platform
import javafx.collections.MapChangeListener
import javafx.collections.ObservableMap
import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.util.AbstractMap.SimpleEntry
import kotlin.collections.Map.Entry

fun <K, V> ObservableMap<K, V>.changesOf(): Flux<MapChange<K, V>> {
    return Flux.create { emitter ->
        val listener = onChanges(emitter)
        emitter.addDisposable(listener.asDisposable(this))
        addListener(listener)
    }
}

fun <K, V> ObservableMap<K, V>.entryAdditions(): Flux<Entry<K, V>> {
    return Flux.create { emitter ->
        val listener = onEntryAddition(emitter)
        emitter.addDisposable(listener.asDisposable(this))
        addListener(listener)
    }
}

fun <K, V> ObservableMap<K, V>.entryRemovals(): Flux<Entry<K, V>> {
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

private fun <K, V> onEntryAddition(emitter: FluxSink<Entry<K, V>>): MapChangeListener<K, V> {
    return MapChangeListener { change ->
        if (change.wasAdded()) {
            emitter.next(SimpleEntry(change.key, change.valueAdded))
        }
    }
}

private fun <K, V> onEntryRemoval(emitter: FluxSink<Entry<K, V>>): MapChangeListener<K, V> {
    return MapChangeListener { change ->
        if (change.wasRemoved()) {
            emitter.next(SimpleEntry(change.key, change.valueRemoved))
        }
    }
}
