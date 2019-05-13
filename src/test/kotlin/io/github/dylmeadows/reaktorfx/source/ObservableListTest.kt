package io.github.dylmeadows.reaktorfx.source

import io.github.dylmeadows.reaktorfx.util.ChangeType
import io.github.dylmeadows.reaktorfx.util.JavaFxExtension
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationTest
import reactor.test.StepVerifier

@ExtendWith(JavaFxExtension::class)
class ObservableListTest : ApplicationTest() {

    private lateinit var testSubject: ObservableList<Any>

    @BeforeEach
    fun setup() {
        testSubject = FXCollections.observableArrayList()
    }

    @Test
    fun `changesOf returns Flux of ListChange`() {
        assertNotNull(testSubject.changesOf())
    }

    @Test
    fun `changesOf emits ListChange value when element is added`() {
        StepVerifier.create(testSubject.changesOf())
            .expectSubscription()
            .then { testSubject.add("abc") }
            .assertNext { change ->
                assertEquals("abc", change.element)
                assertEquals(ChangeType.ADDED, change.changeType)
            }
            .thenCancel()
            .verify()
    }

    @Test
    fun `changesOf emits ListChange value when element is removed`() {
        testSubject.add("abc")

        StepVerifier.create(testSubject.changesOf())
            .expectSubscription()
            .then { testSubject.remove("abc") }
            .assertNext { change ->
                assertEquals("abc", change.element)
                assertEquals(ChangeType.REMOVED, change.changeType)
            }
            .thenCancel()
            .verify()
    }

    @Test
    fun `elementAdditions emits value when element is added`() {
        StepVerifier.create(testSubject.elementAdditions())
            .expectSubscription()
            .then { testSubject.add("abc") }
            .expectNext("abc")
            .thenCancel()
            .verify()
    }

    @Test
    fun `elementRemovals emits value when element is removed`() {
        testSubject.add("abc")

        StepVerifier.create(testSubject.elementRemovals())
            .expectSubscription()
            .then { testSubject.remove("abc") }
            .expectNext("abc")
            .thenCancel()
            .verify()
    }
}