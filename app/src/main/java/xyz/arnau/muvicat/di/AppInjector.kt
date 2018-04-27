package xyz.arnau.muvicat.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import xyz.arnau.muvicat.MuvicatApplication

class AppInjector private constructor() {
    companion object {
        fun init(muvicatApplication: MuvicatApplication) {
            DaggerAppComponent.builder().application(muvicatApplication)
                .build().inject(muvicatApplication)
            muvicatApplication
                .registerActivityLifecycleCallbacks(object :
                    Application.ActivityLifecycleCallbacks {
                    override fun onActivityCreated(
                        activity: Activity?,
                        savedInstanceState: Bundle?
                    ) {
                        activity?.let { handleActivity(it) }
                    }

                    override fun onActivityStarted(activity: Activity?) {}

                    override fun onActivityStopped(activity: Activity?) {}

                    override fun onActivitySaveInstanceState(
                        activity: Activity?,
                        outState: Bundle?
                    ) {
                    }

                    override fun onActivityDestroyed(activity: Activity?) {}

                    override fun onActivityResumed(activity: Activity?) {}

                    override fun onActivityPaused(activity: Activity?) {}
                })
        }

        fun handleActivity(activity: Activity) {
            if (activity is HasSupportFragmentInjector) {
                AndroidInjection.inject(activity)
            }

            if (activity is FragmentActivity) {
                activity.supportFragmentManager
                    .registerFragmentLifecycleCallbacks(
                        object : FragmentManager.FragmentLifecycleCallbacks() {
                            override fun onFragmentCreated(
                                fm: FragmentManager?,
                                f: Fragment?,
                                savedInstanceState: Bundle?
                            ) {
                                if (f is Injectable) {
                                    AndroidSupportInjection.inject(f)
                                }
                            }
                        }, true
                    )
            }
        }
    }
}
