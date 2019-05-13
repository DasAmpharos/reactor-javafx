package io.github.dylmeadows.reaktorfx.source

import io.github.dylmeadows.reaktorfx.util.ChangeType
import javafx.collections.FXCollections
import javafx.collections.ObservableSet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testfx.framework.junit5.ApplicationTest
import reactor.test.StepVerifier

class ObservableSetTest : ApplicationTest() {

    private lateinit var testSubject: ObservableSet<Any>

    @BeforeEach
    fun setup() {
        testSubject = FXCollections.observableSet()
    }

    @Test
    fun `changesOf returns Flux of SetChange`() {
        assertNotNull(testSubject.changesOf())
    }

    @Test
    fun `changesOf emits SetChange value when element is added`() {
        StepVerifier.create(testSubject.changesOf())
            .expectSubscription()
            .then { testSubject.add("abc") }
            .assertNext {
                assertEquals("abc", it.element)
                assertEquals(ChangeType.ADDED, it.changeType)
            }
            .thenCancel()
            .verify()
    }

    @Test
    fun `changesOf emits SetChange value when element is removed`() {
        testSubject.add("abc")

        StepVerifier.create(testSubject.changesOf())
            .expectSubscription()
            .then { testSubject.remove("abc") }
            .assertNext {
                assertEquals("abc", it.element)
                assertEquals(ChangeType.REMOVED, it.changeType)
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