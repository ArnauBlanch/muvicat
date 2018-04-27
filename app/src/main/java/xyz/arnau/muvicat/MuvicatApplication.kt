package xyz.arnau.muvicat

import android.app.Activity
import android.app.Application
import android.content.Context
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import xyz.arnau.muvicat.di.AppInjector
import javax.inject.Inject

open class MuvicatApplication : Application(), HasActivityInjector {
    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        setupTimber()
        AppInjector.init(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        //MultiDex.install(this)
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return activityDispatchingAndroidInjector
    }
}