package xyz.arnau.muvicat.ui.home

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.webkit.WebView
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.about_activity.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.ui.UiPreferencesHelper
import xyz.arnau.muvicat.utils.DateFormatter
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter
import java.util.*
import javax.inject.Inject

class AboutActivity : AppCompatActivity() {
    @Inject
    lateinit var preferencesHelper: UiPreferencesHelper
    @Inject
    lateinit var dateFormatter: DateFormatter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        setContentView(R.layout.about_activity)

        appDescription.text = Html.fromHtml(getString(R.string.app_description))
        setSupportActionBar(aboutToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_chevron_left_white)

        dataConsentButton.setOnClickListener {
            val dialog = ConsentDialogWithAction(this, preferencesHelper)
            dialog.setContentView(layoutInflater.inflate(R.layout.consent_dialog, null))
            dialog.show()
        }

        privacyPolicyButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setView(WebView(this).apply {
                    loadData(
                        streamToString(resources.openRawResource(R.raw.privacy_policy_v1)),
                        "text/html",
                        null
                    )
                })
                .setCancelable(true)
                .create()
                .show()
        }

        usedLibrariesButton.setOnClickListener {
            LibrariesDialog.newInstance().show(fragmentManager, "libraries_dialog")
        }

        dataSourceButton.setOnClickListener {
            DataSourcesDialog.newInstance().apply {
                arguments = Bundle().apply {
                    putString(
                        DataSourcesDialog.GENCAT_UPDATE_DATE, dateFormatter.longDate(
                            Date(preferencesHelper.movieslastUpdateTime)
                        )
                    )
                }
            }
                .show(fragmentManager, "data_sources_dialog")
        }
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
}