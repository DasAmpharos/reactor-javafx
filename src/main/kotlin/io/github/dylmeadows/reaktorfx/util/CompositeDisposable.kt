package io.github.dylmeadows.reaktorfx.util

import reactor.core.Disposable
import java.util.stream.Stream
import kotlin.streams.toList

class CompositeDisposable private constructor(
    private val disposables: MutableList<Disposable>
) : Disposable.Composite {

    private var isDisposed: Boolean = false

    companion object {
        fun of(vararg disposables: Disposable): CompositeDisposable {
            return CompositeDisposable(disposables.toMutableList())
        }

        fun of(disposables: List<Disposable>): CompositeDisposable {
            return CompositeDisposable(disposables.toMutableList())
        }
    }

    override fun dispose() {
        if (!isDisposed) {
            disposables.forEach(Disposable::dispose)
            isDisposed = true
        }
    }

    override fun isDisposed(): Boolean {
        return isDisposed
    }

    override fun add(d: Disposable): Boolean {
        return if (!isDisposed) disposables.add(d) else false
    }

    override fun remove(d: Disposable): Boolean {
        return if (!isDisposed) disposables.remove(d) else false
    }

    override fun size(): Int {
        return disposables.size
    }
}

fun Stream<Disposable>.asComposite(): Disposable {
    return toList().asComposite()
}

fun List<Disposable>.asComposite(): Disposable {
    return CompositeDisposable.of(this)
}

fun Array<Disposable>.asComposite(): Disposable {
    return CompositeDisposable.of(*this)
}