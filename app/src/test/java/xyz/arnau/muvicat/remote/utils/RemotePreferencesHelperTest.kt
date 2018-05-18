package xyz.arnau.muvicat.remote.utils

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import xyz.arnau.muvicat.BuildConfig


@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class RemotePreferencesHelperTest {

    private var preferencesHelper =
        RemotePreferencesHelper(RuntimeEnvironment.application)
    private val sharedPreferences = RuntimeEnvironment.application.getSharedPreferences(
        RemotePreferencesHelper.PREF_BUFFER_PACKAGE_NAME,
        Context.MODE_PRIVATE
    )


    @Test
    fun getMoviesETagReturnETag() {
        val eTag = "\"movies-etag\""
        sharedPreferences.edit().putString(RemotePreferencesHelper.PREF_KEY_MOVIES_ETAG, eTag).apply()

        assertEquals(eTag, preferencesHelper.moviesETag)
    }

    @Test
    fun getCinemasETagReturnETag() {
        val eTag = "\"cinemas-etag\""
        sharedPreferences.edit().putString(RemotePreferencesHelper.PREF_KEY_CINEMAS_ETAG, eTag).apply()

        assertEquals(eTag, preferencesHelper.cinemasETag)
    }

    @Test
    fun getShowingsETagReturnETag() {
        val eTag = "\"showings-etag\""
        sharedPreferences.edit().putString(RemotePreferencesHelper.PREF_KEY_SHOWINGS_ETAG, eTag).apply()

        assertEquals(eTag, preferencesHelper.showingsETag)
    }


    @Test
    fun setMoviesETagSavesETag() {
        val eTag = "\"movies-etag\""
        preferencesHelper.moviesETag = eTag

        assertEquals(
            eTag,
            sharedPreferences.getString(RemotePreferencesHelper.PREF_KEY_MOVIES_ETAG, null)
        )
    }

    @Test
    fun setCinemasETagSavesETag() {
        val eTag = "\"cinemas-etag\""
        preferencesHelper.cinemasETag = eTag

        assertEquals(
            eTag,
            sharedPreferences.getString(RemotePreferencesHelper.PREF_KEY_CINEMAS_ETAG, null)
        )
    }

    @Test
    fun setShowingsETagSavesETag() {
        val eTag = "\"showings-etag\""
        preferencesHelper.showingsETag = eTag

        assertEquals(
            eTag,
            sharedPreferences.getString(RemotePreferencesHelper.PREF_KEY_SHOWINGS_ETAG, null)
        )
    }
}