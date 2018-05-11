package xyz.arnau.muvicat.data.utils

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PowerMockIgnore
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.rule.PowerMockRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import xyz.arnau.muvicat.BuildConfig


@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
@PowerMockIgnore("org.mockito.*", "org.robolectric.*", "android.*")
@PrepareForTest(RepoPreferencesHelper::class)
class RepoPreferencesHelperTest {
    @get:Rule
    val rule = PowerMockRule()

    private var preferencesHelper =
        RepoPreferencesHelper(RuntimeEnvironment.application)
    private val sharedPreferences = RuntimeEnvironment.application.getSharedPreferences(
        RepoPreferencesHelper.PREF_BUFFER_PACKAGE_NAME,
        Context.MODE_PRIVATE
    )

    @Test
    fun getLastMovieCacheTimeReturnsLastTime() {
        val lastTime = 500.toLong()
        sharedPreferences.edit().putLong(RepoPreferencesHelper.PREF_KEY_LAST_MOVIE_UPDATE, lastTime)
            .apply()

        assertEquals(lastTime, preferencesHelper.movieslastUpdateTime)
    }

    @Test
    fun getLastCinemaCacheTimeReturnsLastTime() {
        val lastTime = 500.toLong()
        sharedPreferences.edit().putLong(RepoPreferencesHelper.PREF_KEY_LAST_CINEMA_UPDATE, lastTime)
            .apply()

        assertEquals(lastTime, preferencesHelper.cinemaslastUpdateTime)
    }

    @Test
    fun getLastShowingCacheTimeReturnsLastTime() {
        val lastTime = 500.toLong()
        sharedPreferences.edit().putLong(RepoPreferencesHelper.PREF_KEY_LAST_SHOWING_UPDATE, lastTime)
            .apply()

        assertEquals(lastTime, preferencesHelper.showingslastUpdateTime)
    }

    @Test
    fun moviesUpdatedUpdatesLastMovieUpdateTime() {
        val currentTime = 1000.toLong()
        PowerMockito.mockStatic(System::class.java)
        PowerMockito.`when`(System.currentTimeMillis()).thenReturn(currentTime)
        preferencesHelper.moviesUpdated()

        assertEquals(
            currentTime,
            sharedPreferences.getLong(RepoPreferencesHelper.PREF_KEY_LAST_MOVIE_UPDATE, 0)
        )
    }

    @Test
    fun cinemasUpdatedUpdatesLastCinemaUpdateTime() {
        val currentTime = 1000.toLong()
        PowerMockito.mockStatic(System::class.java)
        PowerMockito.`when`(System.currentTimeMillis()).thenReturn(currentTime)
        preferencesHelper.cinemasUpdated()

        assertEquals(
            currentTime,
            sharedPreferences.getLong(RepoPreferencesHelper.PREF_KEY_LAST_CINEMA_UPDATE, 0)
        )
    }

    @Test
    fun showingsUpdatedUpdatesLastShowingUpdateTime() {
        val currentTime = 1000.toLong()
        PowerMockito.mockStatic(System::class.java)
        PowerMockito.`when`(System.currentTimeMillis()).thenReturn(currentTime)
        preferencesHelper.showingsUpdated()

        assertEquals(
            currentTime,
            sharedPreferences.getLong(RepoPreferencesHelper.PREF_KEY_LAST_SHOWING_UPDATE, 0)
        )
    }
}