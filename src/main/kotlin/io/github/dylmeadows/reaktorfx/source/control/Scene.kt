package io.github.dylmeadows.reaktorfx.source.control

import io.github.dylmeadows.reaktorfx.util.addDisposable
import javafx.event.Event
import javafx.event.EventType
import javafx.scene.Scene
import reactor.core.Disposable
import reactor.core.publisher.Flux

fun <T : Event> Scene.eventsOf(eventType: EventType<T>): Flux<T> {
    return Flux.create { emitter ->
        val handler: (T) -> Unit = { emitter.next(it) }
        emitter.addDisposable(Disposable { removeEventHandler(eventType, handler) })
        addEventHandler(eventType, handler)
    }
}
