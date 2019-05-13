package io.github.dylmeadows.reaktorfx.source

import io.github.dylmeadows.reaktorfx.util.JavaFxExtension
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationTest
import reactor.test.StepVerifier
import java.util.*

@ExtendWith(JavaFxExtension::class)
class ObservableValueTest : ApplicationTest() {

    private lateinit var testSubject: ObjectProperty<Any>

    @BeforeEach
    fun setup() {
        testSubject = SimpleObjectProperty()
    }

    @Test
    fun `valuesOf emits initial value`() {
        testSubject.value = "abc"
        StepVerifier.create(testSubject.valuesOf())
            .expectSubscription()
            .expectNext("abc")
            .thenCancel()
            .verify()
    }

    @Test
    fun `valuesOf emits values as value changes`() {
        StepVerifier.create(testSubject.valuesOf())
            .expectSubscription()
            .then { testSubject.value = "abc" }
            .expectNext("abc")
            .then { testSubject.value = "def" }
            .expectNext("def")
            .thenCancel()
            .verify()
    }

    @Test
    fun `nullablesValuesOf emits initial value of Optional#empty when initial value is null`() {
        StepVerifier.create(testSubject.nullableValuesOf())
            .expectSubscription()
            .expectNext(Optional.empty())
            .thenCancel()
            .verify()
    }

    @Test
    fun `nullableValuesOf emits values wrapped in Optional as value changes`() {
        StepVerifier.create(testSubject.nullableValuesOf())
            .expectSubscription()
            .assertNext { assertFalse(it.isPresent) }
            .then { testSubject.value = "abc" }
            .assertNext {
                assertTrue(it.isPresent)
                assertEquals("abc", it.get())
            }
            .then { testSubject.value = "def" }
            .assertNext {
                assertTrue(it.isPresent)
                assertEquals("def", it.get())
            }
            .then { testSubject.value = null }
            .assertNext { assertFalse(it.isPresent) }
            .thenCancel()
            .verify()
    }
}