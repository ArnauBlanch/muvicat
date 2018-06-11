package xyz.arnau.muvicat.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.privacy_policy.*
import xyz.arnau.muvicat.R
import javax.inject.Inject


class PrivacyPolicyActivity : AppCompatActivity() {
    @Inject
    lateinit var preferencesHelper: UiPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        setContentView(R.layout.privacy_policy)

        acceptButton.setOnClickListener {
            preferencesHelper.privacyPolicyVersion = POLICY_VERSION
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        }
    }

    companion object {
        const val POLICY_VERSION = 1
    }
}