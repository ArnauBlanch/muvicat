package xyz.arnau.muvicat

import android.app.Activity
import dagger.android.DispatchingAndroidInjector
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MuvicatApplicationTest {
    private val injector = mock(DispatchingAndroidInjector::class.java)
    private lateinit var app: MuvicatApplication

    @Before
    fun setUp() {
        app = MuvicatApplication()
        app.activityDispatchingAndroidInjector = injector as DispatchingAndroidInjector<Activity>
    }

    @Test
    fun onCreateTest() {
        app.onCreate()
    }
}