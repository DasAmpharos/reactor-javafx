package io.github.dylmeadows.reaktorfx.source.control

import io.github.dylmeadows.reaktorfx.util.JavaFxExtension
import javafx.event.ActionEvent
import javafx.scene.control.MenuItem
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationTest
import reactor.test.StepVerifier

@ExtendWith(JavaFxExtension::class)
class MenuItemTest : ApplicationTest() {

    private lateinit var testSubject: MenuItem

    @BeforeEach
    fun setup() {
        testSubject = MenuItem()
    }

    @Test
    fun `eventsOf emits values as events occur`() {
        StepVerifier.create(testSubject.eventsOf(ActionEvent.ACTION))
            .expectSubscription()
            .then { testSubject.fire() }
            .assertNext {
                assertEquals(testSubject, it.source)
                assertEquals(testSubject, it.target)
                assertEquals(ActionEvent.ACTION, it.eventType)
            }
            .thenCancel()
            .verify()
    }
}