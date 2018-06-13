package xyz.arnau.muvicat.ui.home

import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.consent_dialog.*
import xyz.arnau.muvicat.ui.ConsentReceiver
import xyz.arnau.muvicat.ui.UiPreferencesHelper

class ConsentDialogWithoutAction(
    activity: AppCompatActivity,
    preferencesHelper: UiPreferencesHelper,
    firstTime: Boolean,
    private val receiver: ConsentReceiver
) :
    ConsentDialog(activity, preferencesHelper, firstTime) {
    override fun processConsent(initialUsageStats: Boolean, initialCrashReports: Boolean) {
        receiver.receiveUsageConsent(
            when {
                usage_stats.isChecked == initialUsageStats -> ConsentStatus.NOT_MODIFIED
                usage_stats.isChecked -> ConsentStatus.ENABLE
                else -> ConsentStatus.DISABLE
            }
        )
        receiver.receiveCrashReportingConsent(
            when {
                crash_reports.isChecked == initialCrashReports -> ConsentStatus.NOT_MODIFIED
                crash_reports.isChecked -> ConsentStatus.ENABLE
                else -> ConsentStatus.DISABLE
            }
        )
    }
}