package io.github.dylmeadows.reaktorfx.util

import javafx.application.Platform
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.InvocationInterceptor
import org.junit.jupiter.api.extension.InvocationInterceptor.Invocation
import org.junit.jupiter.api.extension.ReflectiveInvocationContext
import java.lang.reflect.Method
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference

class JavaFxExtension : InvocationInterceptor {

    override fun interceptTestMethod(
        invocation: Invocation<Void>,
        invocationContext: ReflectiveInvocationContext<Method>,
        extensionContext: ExtensionContext
    ) {
        val latch = CountDownLatch(1)
        val error = AtomicReference<Throwable?>()
        Platform.runLater {
            error.capture {
                invocation.proceed()
            }
            latch.countDown()
        }
        latch.await()
        error.tryPropagate()
    }
}

private fun AtomicReference<Throwable?>.capture(block: () -> Unit) {
    try {
        block()
    } catch (e: Throwable) {
        set(e)
    }
}

private fun AtomicReference<Throwable?>.tryPropagate() {
    val error = get()
    if (error != null) {
        throw error
    }
}