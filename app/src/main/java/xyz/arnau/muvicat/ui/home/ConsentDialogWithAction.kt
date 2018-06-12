package xyz.arnau.muvicat.ui.home

import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.consent_dialog.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.ui.UiPreferencesHelper

class ConsentDialogWithAction(
    private val activity: AppCompatActivity,
    private val preferencesHelper: UiPreferencesHelper
) :
    ConsentDialog(activity, preferencesHelper, false) {
    override fun processConsent(initialUsageStats: Boolean, initialCrashReports: Boolean) {
        if (initialUsageStats != usage_stats.isChecked) {
            if (usage_stats.isChecked) {
                FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(true)
            } else {
                FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(false)
                FirebaseAnalytics.getInstance(context).resetAnalyticsData()
            }
            preferencesHelper.consentUsageStats = usage_stats.isChecked
        }
        if (initialCrashReports != crash_reports.isChecked) {
            if (crash_reports.isChecked) {
                Fabric.with(context, Crashlytics())
            } else {
                Snackbar.make(
                    activity.findViewById(android.R.id.content),
                    context.getString(R.string.crash_reports_disabled_message),
                    Snackbar.LENGTH_LONG
                ).show()
            }
            preferencesHelper.consentCrashReporting = crash_reports.isChecked
        }
    }
}