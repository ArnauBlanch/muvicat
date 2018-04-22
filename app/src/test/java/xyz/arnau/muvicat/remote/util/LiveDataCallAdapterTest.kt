package xyz.arnau.muvicat.remote.util

import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import java.lang.reflect.Type

@RunWith(JUnit4::class)
class LiveDataAdapterTest {
    @Test
    fun testResponseType() {
        val responseType = mock(Type::class.java)
        val adapter = LiveDataCallAdapter<Int>(responseType)

        assertEquals(responseType, adapter.responseType())
    }
}
