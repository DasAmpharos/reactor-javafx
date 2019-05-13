package io.github.dylmeadows.reaktorfx.util

typealias ErrorHandler = (Throwable) -> Unit

object ErrorHandlers {
    val doNothing: ErrorHandler = {}
}