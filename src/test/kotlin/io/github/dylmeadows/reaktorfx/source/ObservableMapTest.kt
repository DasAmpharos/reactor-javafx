package io.github.dylmeadows.reaktorfx.source

import io.github.dylmeadows.reaktorfx.util.ChangeType
import io.github.dylmeadows.reaktorfx.util.JavaFxExtension
import javafx.collections.FXCollections
import javafx.collections.ObservableMap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationTest
import reactor.test.StepVerifier

@ExtendWith(JavaFxExtension::class)
class ObservableMapTest : ApplicationTest() {

    private lateinit var testSubject: ObservableMap<Any, Any>

    @BeforeEach
    fun setup() {
        testSubject = FXCollections.observableHashMap()
    }

    @Test
    fun `changesOf returns Flux of MapChange`() {
        assertNotNull(testSubject.changesOf())
    }

    @Test
    fun `changesOf emits MapChange when element is added`() {
        StepVerifier.create(testSubject.changesOf())
            .expectSubscription()
            .then { testSubject["abc"] = "123" }
            .assertNext {
                assertEquals("abc", it.key)
                assertEquals("123", it.value)
                assertEquals(ChangeType.ADDED, it.changeType)
            }
            .thenCancel()
            .verify()
    }

    @Test
    fun `changesOf emits MapChange when element is removed`() {
        testSubject["abc"] = "123"

        StepVerifier.create(testSubject.changesOf())
            .expectSubscription()
            .then { testSubject.remove("abc") }
            .assertNext {
                assertEquals("abc", it.key)
                assertEquals("123", it.value)
                assertEquals(ChangeType.REMOVED, it.changeType)
            }
            .thenCancel()
            .verify()
    }

    @Test
    fun `entryAdditions emits KeyValuePair when entry is added`() {
        StepVerifier.create(testSubject.entryAdditions())
            .expectSubscription()
            .then { testSubject["abc"] = "123" }
            .assertNext {
                assertEquals("abc", it.key)
                assertEquals("123", it.value)
            }
            .thenCancel()
            .verify()
    }

    @Test
    fun `entryRemovals emits KeyValuePair when entry is removed`() {
        testSubject["abc"] = "123"

        StepVerifier.create(testSubject.entryRemovals())
            .expectSubscription()
            .then { testSubject.remove("abc") }
            .assertNext {
                assertEquals("abc", it.key)
                assertEquals("123", it.value)
            }
            .thenCancel()
            .verify()
    }
}