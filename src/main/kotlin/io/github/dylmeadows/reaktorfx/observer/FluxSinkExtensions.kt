package io.github.dylmeadows.reaktorfx.observer

import reactor.core.Disposable
import reactor.core.publisher.FluxSink

fun <T> FluxSink<T>.addDisposable(disposable: Disposable) {
    onDispose(disposable)
    onCancel(disposable)
}
