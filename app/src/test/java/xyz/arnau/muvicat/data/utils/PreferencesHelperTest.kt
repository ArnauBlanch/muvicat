package xyz.arnau.muvicat.data.utils

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PowerMockIgnore
import org.powermock.core.classloader.annotations.PrepareForTest
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import xyz.arnau.muvicat.BuildConfig
import org.junit.Rule
import org.powermock.modules.junit4.rule.PowerMockRule


@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [16])
@PowerMockIgnore("org.mockito.*", "org.robolectric.*", "android.*")
@PrepareForTest(PreferencesHelper::class)
class PreferencesHelperTest {
    @get:Rule
    val rule = PowerMockRule()

    private var preferencesHelper = PreferencesHelper(RuntimeEnvironment.application)
    private val sharedPreferences = RuntimeEnvironment.application.getSharedPreferences(
            PreferencesHelper.PREF_BUFFER_PACKAGE_NAME,
            Context.MODE_PRIVATE
    )

    @Test
    fun getLastCacheTimeReturnsLastTime() {
        val lastTime = 500.toLong()
        sharedPreferences.edit().putLong(PreferencesHelper.PREF_KEY_LAST_MOVIE_UPDATE, lastTime).apply()

        assertEquals(lastTime, preferencesHelper.movieslastUpdateTime)
    }

    @Test
    fun moviesUpdatedUpdatesLastMovieUpdateTime() {
        val currentTime = 1000.toLong()
        PowerMockito.mockStatic(System::class.java)
        PowerMockito.`when`(System.currentTimeMillis()).thenReturn(currentTime)
        preferencesHelper.moviesUpdated()

        assertEquals(currentTime, sharedPreferences.getLong(PreferencesHelper.PREF_KEY_LAST_MOVIE_UPDATE, 0))
    }

    @Test
    fun getMoviesETagReturnETag() {
        val eTag = "\"movies-etag\""
        sharedPreferences.edit().putString(PreferencesHelper.PREF_KEY_MOVIES_ETAG, eTag).apply()

        assertEquals(eTag, preferencesHelper.moviesETag)
    }

    @Test
    fun setMoviesETagSavesETag() {
        val eTag = "\"movies-etag\""
        preferencesHelper.moviesETag = eTag

        assertEquals(eTag, sharedPreferences.getString(PreferencesHelper.PREF_KEY_MOVIES_ETAG, null))
    }
}