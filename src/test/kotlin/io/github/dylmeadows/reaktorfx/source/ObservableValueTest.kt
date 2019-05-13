package io.github.dylmeadows.reaktorfx.source

import io.github.dylmeadows.reaktorfx.util.JavaFxExtension
import io.github.dylmeadows.reaktorfx.util.any
import io.github.dylmeadows.reaktorfx.util.mock
import io.github.dylmeadows.reaktorfx.util.whenever
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.verify
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
    fun `nullableValuesOf emits initial value of Optional#empty when initial value is null`() {
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

    @Test
    fun `changesOf emits old and new values on value change`() {
        StepVerifier.create(testSubject.changesOf())
            .expectSubscription()
            .then { testSubject.value = "abc" }
            .assertNext {
                assertNull(it.oldValue)
                assertEquals("abc", it.newValue)
            }
            .then { testSubject.value = "def" }
            .assertNext {
                assertEquals("abc", it.oldValue)
                assertEquals("def", it.newValue)
            }
            .then { testSubject.value = null }
            .assertNext {
                assertEquals("def", it.oldValue)
                assertNull(it.newValue)
            }
            .thenCancel()
            .verify()
    }

    @Test
    fun `invalidationsOf emits the observable when value is changed`() {
        StepVerifier.create(testSubject.invalidationsOf())
            .expectSubscription()
            .then { testSubject.value = "abc" }
            .expectNext(testSubject)
            .thenCancel()
            .verify()
    }

    @Test
    fun `combineWith with p2 invokes combinator whenever p1 or p2 changes`() {
        val combinator = mock<(Any?, Any?) -> Any>()
        whenever(combinator.invoke(any(), any())).thenReturn(Unit)
        val p2 = SimpleObjectProperty<Any>()

        StepVerifier.create(testSubject.combineWith(p2, combinator))
            .expectSubscription()
            .assertNext { verify(combinator).invoke(eq(null), eq(null)) }
            .then { testSubject.value = "abc" }
            .assertNext { verify(combinator).invoke(eq("abc"), eq(null)) }
            .then { p2.value = "def" }
            .assertNext { verify(combinator).invoke(eq("abc"), eq("def")) }
            .thenCancel()
            .verify()
    }

    @Test
    fun `combineWith with p2 and p3 invokes combinator whenever p1, p2 or p3 changes`() {
        val combinator = mock<(Any?, Any?, Any?) -> Any>()
        whenever(combinator.invoke(any(), any(), any())).thenReturn(Unit)
        val p2 = SimpleObjectProperty<Any>()
        val p3 = SimpleObjectProperty<Any>()

        StepVerifier.create(testSubject.combineWith(p2, p3, combinator))
            .expectSubscription()
            .assertNext { verify(combinator).invoke(eq(null), eq(null), eq(null)) }
            .then { testSubject.value = "abc" }
            .assertNext { verify(combinator).invoke(eq("abc"), eq(null), eq(null)) }
            .then { p2.value = "def" }
            .assertNext { verify(combinator).invoke(eq("abc"), eq("def"), eq(null)) }
            .then { p3.value = "ghi" }
            .assertNext { verify(combinator).invoke(eq("abc"), eq("def"), eq("ghi")) }
            .thenCancel()
            .verify()
    }

    @Test
    fun `combineWith with p2, p3 and p4 invokes combinator whenever p1, p2, p3 or p4 changes`() {
        val combinator = mock<(Any?, Any?, Any?, Any?) -> Any>()
        whenever(combinator.invoke(any(), any(), any(), any())).thenReturn(Unit)
        val p2 = SimpleObjectProperty<Any>()
        val p3 = SimpleObjectProperty<Any>()
        val p4 = SimpleObjectProperty<Any>()

        StepVerifier.create(testSubject.combineWith(p2, p3, p4, combinator))
            .expectSubscription()
            .assertNext { verify(combinator).invoke(eq(null), eq(null), eq(null), eq(null)) }
            .then { testSubject.value = "abc" }
            .assertNext { verify(combinator).invoke(eq("abc"), eq(null), eq(null), eq(null)) }
            .then { p2.value = "def" }
            .assertNext { verify(combinator).invoke(eq("abc"), eq("def"), eq(null), eq(null)) }
            .then { p3.value = "ghi" }
            .assertNext { verify(combinator).invoke(eq("abc"), eq("def"), eq("ghi"), eq(null)) }
            .then { p4.value = "jkl" }
            .assertNext { verify(combinator).invoke(eq("abc"), eq("def"), eq("ghi"), eq("jkl")) }
            .thenCancel()
            .verify()
    }
}