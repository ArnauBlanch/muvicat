package xyz.arnau.muvicat.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.android.AndroidInjection
import io.fabric.sdk.android.Fabric
import xyz.arnau.muvicat.MuvicatApplication
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {
    @Inject
    lateinit var preferencesHelper: UiPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        when {
            (application as MuvicatApplication).isInFirebaseTestLab ->
                startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            preferencesHelper.privacyPolicyVersion < PrivacyPolicyActivity.POLICY_VERSION ->
                startActivity(Intent(this, PrivacyPolicyActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            else -> {
                if (preferencesHelper.consentUsageStats)
                    FirebaseAnalytics.getInstance(applicationContext).setAnalyticsCollectionEnabled(true)
                if (preferencesHelper.consentCrashReporting)
                    Fabric.with(applicationContext, Crashlytics())
                startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }

        finish()
    }
}