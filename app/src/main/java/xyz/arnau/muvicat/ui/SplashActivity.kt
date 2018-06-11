package xyz.arnau.muvicat.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {
    @Inject
    lateinit var preferencesHelper: UiPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        if (preferencesHelper.privacyPolicyVersion < PrivacyPolicyActivity.POLICY_VERSION)
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
        else
            startActivity(Intent(this, MainActivity::class.java))

        finish()
    }
}