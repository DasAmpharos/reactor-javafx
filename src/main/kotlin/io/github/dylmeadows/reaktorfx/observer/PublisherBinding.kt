package io.github.dylmeadows.reaktorfx.observer

import io.github.dylmeadows.reaktorfx.util.ErrorHandler
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.SimpleObjectProperty
import org.reactivestreams.Publisher
import reactor.core.Disposable
import reactor.core.publisher.BaseSubscriber

class PublisherBinding<T>(publisher: Publisher<T>, onError: ErrorHandler) : ObjectBinding<T>() {

    val isDisposed: Boolean get() = disposable.isDisposed
    private val property = SimpleObjectProperty<T>(this, "PublisherBinding")
    private val disposable: Disposable

    init {
        disposable = object : BaseSubscriber<T>() {
            override fun hookOnNext(value: T) {
                property.value = value
                invalidate()
            }

            override fun hookOnError(throwable: Throwable) {
                onError.invoke(throwable)
            }
        }
        publisher.subscribe(disposable)
    }

    override fun computeValue(): T = property.value

    override fun dispose() {
        disposable.dispose()
    }
}