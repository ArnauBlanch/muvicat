package xyz.arnau.muvicat.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.consent_dialog.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.ui.UiPreferencesHelper

abstract class ConsentDialog(
    activity: AppCompatActivity,
    private val preferencesHelper: UiPreferencesHelper,
    private val firstTime: Boolean
) :
    BottomSheetDialog(activity) {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (firstTime) {
            usage_stats.isChecked = true
            crash_reports.isChecked = true
        } else {
            usage_stats.isChecked = preferencesHelper.consentUsageStats
            crash_reports.isChecked = preferencesHelper.consentCrashReporting
        }

        val initialUsageStats = preferencesHelper.consentUsageStats
        val initialCrashReports = preferencesHelper.consentCrashReporting

        okayButton.setOnClickListener {
            processConsent(initialUsageStats, initialCrashReports)
            dismiss()
        }
    }

    abstract fun processConsent(initialUsageStats: Boolean, initialCrashReports: Boolean)
}