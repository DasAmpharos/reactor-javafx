package io.github.dylmeadows.reaktorfx.source.control

import io.github.dylmeadows.reaktorfx.observer.addDisposable
import javafx.event.Event
import javafx.event.EventType
import javafx.scene.control.MenuItem
import reactor.core.Disposable
import reactor.core.publisher.Flux

fun <T : Event> MenuItem.eventsOf(eventType: EventType<T>): Flux<T> {
    return Flux.create { emitter ->
        val handler: (T) -> Unit = { emitter.next(it) }
        emitter.addDisposable(Disposable { removeEventHandler(eventType, handler) })
        addEventHandler(eventType, handler)
    }
}