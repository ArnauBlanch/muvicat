package xyz.arnau.muvicat

import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import java.util.concurrent.Executor

class AppExecutorsTest {
    private val diskIO = mock(Executor::class.java)
    private val networkIO = mock(Executor::class.java)
    private val mainThread = mock(Executor::class.java)
    private val appExecutors = AppExecutors(diskIO, networkIO, mainThread)

    @Test
    fun diskIOReturnsExecutor() {
        assertEquals(diskIO, appExecutors.diskIO())
    }

    @Test
    fun networkIOReturnsExecutor() {
        assertEquals(networkIO, appExecutors.networkIO())
    }

    @Test
    fun mainThreadReturnsExecutor() {
        assertEquals(mainThread, appExecutors.mainThread())
    }
}