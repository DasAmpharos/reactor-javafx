package io.github.dylmeadows.reaktorfx.source.control

import io.github.dylmeadows.reaktorfx.util.JavaFxExtension
import io.github.dylmeadows.reaktorfx.util.mock
import io.github.dylmeadows.reaktorfx.util.whenever
import javafx.scene.control.Dialog
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationTest
import reactor.test.StepVerifier
import java.util.*

@ExtendWith(JavaFxExtension::class)
class DialogTest : ApplicationTest() {

    private lateinit var testSubject: Dialog<Any>

    @BeforeEach
    fun setup() {
        testSubject = mock()
    }

    @Test
    fun `resultOf emits value when showAndWait returns non-empty Optional`() {
        whenever(testSubject.showAndWait()).thenReturn(Optional.of("abc"))

        StepVerifier.create(testSubject.resultOf())
            .expectSubscription()
            .expectNext("abc")
            .verifyComplete()
    }

    @Test
    fun `resultOf emits complete when showAndWait returns empty Optional`() {
        whenever(testSubject.showAndWait()).thenReturn(Optional.empty())

        StepVerifier.create(testSubject.resultOf())
            .expectSubscription()
            .verifyComplete()
    }
}