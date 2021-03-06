package xyz.arnau.muvicat

import android.app.Activity
import android.provider.Settings
import android.support.multidex.MultiDexApplication
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import xyz.arnau.muvicat.di.AppInjector
import javax.inject.Inject

class MuvicatApplication : MultiDexApplication(), HasActivityInjector {
    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    var isInFirebaseTestLab: Boolean = false
        private set
    var hasRequestedForLocationPermission = false

    override fun onCreate() {
        super.onCreate()
        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        */
        setupDebug()
        AppInjector.init(this)
        val testLabSetting =
            Settings.System.getString(contentResolver, "firebase.test.lab")
        if ("true" == testLabSetting) {
            // Do something when running in Test Lab
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false)
            isInFirebaseTestLab = true
        }
    }

    private fun setupDebug() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return activityDispatchingAndroidInjector
    }
}