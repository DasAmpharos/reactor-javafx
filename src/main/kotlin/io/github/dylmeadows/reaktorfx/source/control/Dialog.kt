package io.github.dylmeadows.reaktorfx.source.control

import javafx.scene.control.Dialog
import reactor.core.publisher.Mono

fun <T> Dialog<T>.showAndWait(): Mono<T> {
    return Mono.fromCallable(::showAndWait)
        .flatMap { Mono.justOrEmpty(it) }
}