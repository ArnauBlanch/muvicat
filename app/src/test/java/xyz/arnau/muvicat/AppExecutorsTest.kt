package xyz.arnau.muvicat

import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import xyz.arnau.muvicat.utils.AppExecutors
import java.util.concurrent.Executor

@RunWith(RobolectricTestRunner::class)
class AppExecutorsTest {
    private val diskIO = mock(Executor::class.java)
    private val networkIO = mock(Executor::class.java)
    private val mainThread = mock(Executor::class.java)
    private val appExecutors = AppExecutors(diskIO, networkIO, mainThread)

    @Test
    fun constructor() {
        AppExecutors()
    }

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