package xyz.arnau.muvicat.ui

import android.content.Context
import android.content.SharedPreferences
import xyz.arnau.muvicat.repository.utils.RepoPreferencesHelper
import javax.inject.Singleton

@Singleton
class UiPreferencesHelper constructor(context: Context) {

    companion object {
        const val PREF_BUFFER_PACKAGE_NAME = "xyz.arnau.muvicat.preferences"

        const val PREF_KEY_NEARBY_SHOWINGS_DISTANCE = "nearby_showings_distance"
        const val PREF_KEY_PRIVACY_POLICY_VERSION = "privacy_policy_version"
        const val PREF_KEY_USAGE_STATS_CONSENT = "consent_usage_stats"
        const val PREF_KEY_CRASH_REPORTING_CONSENT = "consent_crash_reporting"

        const val PREF_KEY_LAST_MOVIE_UPDATE = "last_movie_update"
    }

    private val bufferPref: SharedPreferences

    init {
        bufferPref = context.getSharedPreferences(PREF_BUFFER_PACKAGE_NAME, Context.MODE_PRIVATE)
    }

    val movieslastUpdateTime: Long
        get() = bufferPref.getLong(RepoPreferencesHelper.PREF_KEY_LAST_MOVIE_UPDATE, 0)

    var nearbyShowingsDistance: Int
        get() = bufferPref.getInt(PREF_KEY_NEARBY_SHOWINGS_DISTANCE, 35)
        set(distance) {
            bufferPref.edit().putInt(PREF_KEY_NEARBY_SHOWINGS_DISTANCE, distance).apply()
        }

    var privacyPolicyVersion: Int
        get() = bufferPref.getInt(PREF_KEY_PRIVACY_POLICY_VERSION, 0)
        set(version) {
            bufferPref.edit().putInt(PREF_KEY_PRIVACY_POLICY_VERSION, version).apply()
        }


    var consentUsageStats: Boolean
        get() = bufferPref.getBoolean(PREF_KEY_USAGE_STATS_CONSENT, false)
        set(consent) {
            bufferPref.edit().putBoolean(PREF_KEY_USAGE_STATS_CONSENT, consent).apply()
        }

    var consentCrashReporting: Boolean
        get() = bufferPref.getBoolean(PREF_KEY_CRASH_REPORTING_CONSENT, false)
        set(consent) {
            bufferPref.edit().putBoolean(PREF_KEY_CRASH_REPORTING_CONSENT, consent).apply()
        }
}
