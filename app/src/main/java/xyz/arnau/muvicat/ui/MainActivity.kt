package xyz.arnau.muvicat.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.ui.movie.MovieListFragment
import javax.inject.Inject


class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val viewPagerAdapter = ViewPagerAdapter(listOf(
        MovieListFragment(),
        Fragment()
    ), supportFragmentManager)

    private val FRAG_MOVIES = 0
    private val FRAG_CINEMAS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        setupBottomNavigation()

        fragmentsViewPager.adapter = viewPagerAdapter
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_movies -> {
                    fragmentsViewPager.setCurrentItem(FRAG_MOVIES, false)
                    true
                }
                R.id.action_cinemas -> {
                    fragmentsViewPager.setCurrentItem(FRAG_CINEMAS, false)
                    true
                }
                else -> false
            }
        }
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }
}