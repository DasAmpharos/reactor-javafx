package io.github.dylmeadows.reaktorfx.source.control

import io.github.dylmeadows.reaktorfx.util.JavaFxExtension
import javafx.event.ActionEvent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.stage.Stage
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationTest
import reactor.test.StepVerifier

@ExtendWith(JavaFxExtension::class)
class NodeTest : ApplicationTest() {

    private lateinit var testSubject: Button

    override fun start(stage: Stage) {
        testSubject = Button("Button")
        stage.scene = Scene(testSubject)
        stage.show()
    }

    @Test
    fun `eventsOf emits values as events occur`() {
        StepVerifier.create(testSubject.eventsOf(ActionEvent.ACTION))
            .expectSubscription()
            .then { testSubject.fire() }
            .assertNext {
                Assert.assertEquals(testSubject, it.source)
                Assert.assertEquals(testSubject, it.target)
                Assert.assertEquals(ActionEvent.ACTION, it.eventType)
            }
            .thenCancel()
            .verify()
    }
}