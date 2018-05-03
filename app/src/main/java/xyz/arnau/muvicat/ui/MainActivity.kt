package xyz.arnau.muvicat.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.ui.cinema.CinemaListFragment
import xyz.arnau.muvicat.ui.movie.MovieListFragment
import javax.inject.Inject


class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val viewPagerAdapter = ViewPagerAdapter(
        listOf(
            MovieListFragment(),
            CinemaListFragment()
        ), supportFragmentManager
    )

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
                    fragmentsViewPager.setCurrentItem(MovieListFragment.FRAG_ID, false)
                    FirebaseAnalytics.getInstance(this)
                            .setCurrentScreen(this, "Movie list", "Movie list")
                    true
                }
                R.id.action_cinemas -> {
                    fragmentsViewPager.setCurrentItem(CinemaListFragment.FRAG_ID, false)
                    FirebaseAnalytics.getInstance(this)
                        .setCurrentScreen(this, "Cinema list", "Cinema list")
                    true
                }
                else -> false
            }
        }
    }

    fun isSelectedFragment(fragment: Int): Boolean =
        fragment == fragmentsViewPager.currentItem

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }
}