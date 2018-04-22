package xyz.arnau.muvicat.data

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import xyz.arnau.muvicat.data.PreferencesHelper


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [16])
class PreferencesHelperTest {
    private var preferencesHelper = PreferencesHelper(RuntimeEnvironment.application)
    private val sharedPreferences = RuntimeEnvironment.application.getSharedPreferences(
            PreferencesHelper.PREF_BUFFER_PACKAGE_NAME,
            Context.MODE_PRIVATE
    )

    @Test
    fun getLastCacheTimeReturnsLastTime() {
        val lastTime = 500.toLong()
        sharedPreferences.edit().putLong(PreferencesHelper.PREF_KEY_LAST_CACHE, lastTime).apply()

        assertEquals(lastTime, preferencesHelper.lastCacheTime)
    }

    @Test
    fun setLastCacheTimeSavedLastTime() {
        val lastTime = 300.toLong()
        preferencesHelper.lastCacheTime = lastTime

        assertEquals(300, sharedPreferences.getLong(PreferencesHelper.PREF_KEY_LAST_CACHE, 0))
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