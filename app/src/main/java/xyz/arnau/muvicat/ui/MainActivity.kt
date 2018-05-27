package xyz.arnau.muvicat.ui

import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.ui.cinema.CinemaListFragment
import xyz.arnau.muvicat.ui.movie.MovieListFragment
import xyz.arnau.muvicat.ui.showing.ShowingListFragment
import javax.inject.Inject


class MainActivity : LocationAwareActivity(), HasSupportFragmentInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val viewPagerAdapter = ViewPagerAdapter(
        listOf(
            MovieListFragment(),
            ShowingListFragment(),
            CinemaListFragment()
        ), supportFragmentManager
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        setupBottomNavigation()

        fragmentsViewPager.adapter = viewPagerAdapter
        fragmentsViewPager.offscreenPageLimit = 4
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_movies -> {
                    fragmentsViewPager.setCurrentItemOrScrollToTop(MovieListFragment.FRAG_ID, false)

                    FirebaseAnalytics.getInstance(this)
                        .setCurrentScreen(this, "Movie list", "Movie list")
                    true
                }
                R.id.action_showings -> {
                    fragmentsViewPager.setCurrentItemOrScrollToTop(
                        ShowingListFragment.FRAG_ID,
                        false
                    )

                    FirebaseAnalytics.getInstance(this)
                        .setCurrentScreen(this, "Showing list", "Showing list")
                    true
                }
                R.id.action_cinemas -> {
                    fragmentsViewPager.setCurrentItemOrScrollToTop(
                        CinemaListFragment.FRAG_ID,
                        false
                    )
                    FirebaseAnalytics.getInstance(this)
                        .setCurrentScreen(this, "Cinema list", "Cinema list")
                    true
                }
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        val currentFragment = viewPagerAdapter.getItem(fragmentsViewPager.currentItem)
        if (currentFragment is BackPressable)
            currentFragment.onBackPressed()
        else
            super.onBackPressed()
    }

    fun isSelectedFragment(fragment: Int): Boolean =
        fragment == fragmentsViewPager.currentItem

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }

    override fun processLastLocation(location: Location) {
        (viewPagerAdapter.getItem(CinemaListFragment.FRAG_ID) as CinemaListFragment)
            .notifyLastLocation(location)
    }
}