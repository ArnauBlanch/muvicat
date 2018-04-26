package xyz.arnau.muvicat.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import xyz.arnau.muvicat.data.utils.PreferencesHelper
import xyz.arnau.muvicat.utils.DateFormatter

@RunWith(RobolectricTestRunner::class)
class AppModuleTest {
    private val appModule = AppModule()

    @Test
    fun testProvideContext() {
        val app = mock(Application::class.java)
        assertEquals(app, appModule.provideContext(app))
    }

    @Test
    fun testProvidePreferencesHelper() {
        val app = mock(Application::class.java)
        `when`(app.getSharedPreferences(PreferencesHelper.PREF_BUFFER_PACKAGE_NAME, Context.MODE_PRIVATE)).thenReturn(mock(SharedPreferences::class.java))
        assertEquals(PreferencesHelper::class.java, PreferencesHelper(app).javaClass)
    }

    @Test
    fun testProvideDateFormatter() {
        val app = mock(Context::class.java)
        assertEquals(DateFormatter::class.java, DateFormatter(app).javaClass)
    }
}