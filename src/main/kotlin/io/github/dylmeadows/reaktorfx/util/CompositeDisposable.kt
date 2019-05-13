package io.github.dylmeadows.reaktorfx.util

import reactor.core.Disposable

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