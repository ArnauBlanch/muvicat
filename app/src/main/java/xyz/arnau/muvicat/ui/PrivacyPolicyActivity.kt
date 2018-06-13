package xyz.arnau.muvicat.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.android.AndroidInjection
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.consent_dialog.*
import kotlinx.android.synthetic.main.privacy_policy.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.ui.home.ConsentDialogWithoutAction
import xyz.arnau.muvicat.ui.home.ConsentStatus
import xyz.arnau.muvicat.ui.home.ConsentStatus.DISABLE
import xyz.arnau.muvicat.ui.home.ConsentStatus.ENABLE
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter
import javax.inject.Inject


class PrivacyPolicyActivity : AppCompatActivity(), ConsentReceiver {
    @Inject
    lateinit var preferencesHelper: UiPreferencesHelper

    private var usageStatus: ConsentStatus = ENABLE
    private var crashReportingStatus: ConsentStatus = ENABLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        setContentView(R.layout.privacy_policy)
        webView.setBackgroundColor(0x00000000)
        webView.loadData(streamToString(resources.openRawResource(R.raw.privacy_policy_v1)), "text/html; charset=UTF-8", null)

        acceptButton.setOnClickListener {
            preferencesHelper.privacyPolicyVersion = POLICY_VERSION
            if (usageStatus == ENABLE) {
                FirebaseAnalytics.getInstance(application).setAnalyticsCollectionEnabled(true)
            } else if (usageStatus == DISABLE){
                FirebaseAnalytics.getInstance(application).setAnalyticsCollectionEnabled(false)
                FirebaseAnalytics.getInstance(application).resetAnalyticsData()
            }
            preferencesHelper.consentUsageStats = usageStatus == ENABLE

            if (crashReportingStatus == ENABLE) {
                Fabric.with(application, Crashlytics())
            }
            preferencesHelper.consentCrashReporting = crashReportingStatus == ENABLE

            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        }

        optionsButton.setOnClickListener {
            val dialog = ConsentDialogWithoutAction(this, preferencesHelper, true, this)
            dialog.setContentView(layoutInflater.inflate(R.layout.consent_dialog, null))
            dialog.show()
        }
    }

    override fun receiveCrashReportingConsent(status: ConsentStatus) {
        crashReportingStatus = status
    }

    override fun receiveUsageConsent(status: ConsentStatus) {
        usageStatus = status
    }

    private fun streamToString(`in`: InputStream?): String {
        if (`in` == null) {
            return ""
        }
        val writer = StringWriter()
        val buffer = CharArray(1024)
        try {
            val reader = BufferedReader(InputStreamReader(`in`, "UTF-8"))
            var n: Int = reader.read(buffer)
            while (n != -1) {
                writer.write(buffer, 0, n)
                n = reader.read(buffer)
            }
        } finally {
        }
        return writer.toString()
    }

    companion object {
        const val POLICY_VERSION = 1
    }
}