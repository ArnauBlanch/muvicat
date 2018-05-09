package xyz.arnau.muvicat.cache.db

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import xyz.arnau.muvicat.AppExecutors

@RunWith(JUnit4::class)
class MuvicatDatabaseTest {
    private val context = mock(Context::class.java)
    private val appExecutors = mock(AppExecutors::class.java)

    @Test
    fun getInstanceReturnsInstance() {
        assertEquals(
            "${MuvicatDatabase::class.java.canonicalName}_Impl",
            MuvicatDatabase.getInstance(context, appExecutors)::class.java.canonicalName
        )
    }
}