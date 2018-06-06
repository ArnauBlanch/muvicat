package xyz.arnau.muvicat.ui.home

import android.app.Activity
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.home_fragment.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.di.Injectable
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.repository.model.Resource
import xyz.arnau.muvicat.repository.model.Status
import xyz.arnau.muvicat.ui.MainActivity
import xyz.arnau.muvicat.ui.utils.ScrollableToTop
import xyz.arnau.muvicat.ui.utils.ViewPagerAdapter
import xyz.arnau.muvicat.utils.setGone
import xyz.arnau.muvicat.utils.setVisible
import xyz.arnau.muvicat.viewmodel.movie.MovieListViewModel
import javax.inject.Inject

class HomeFragment : Fragment(), Injectable, ScrollableToTop {
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    @Inject
    lateinit var viewModel: MovieListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    // TODO: page indicator

    override fun onStart() {
        super.onStart()
        viewPagerAdapter = ViewPagerAdapter(listOf(), childFragmentManager)
        trailerViewPager.adapter = viewPagerAdapter

        viewModel.movies.observe(this,
            Observer<Resource<List<Movie>>> {
                if (it != null) handleDataState(it.status, it.data)
            })
    }

    // TODO: rate movie a Firebase Analytics
    private fun handleDataState(status: Status, data: List<Movie>?) {
        errorMessage.setGone()
        if (status == Status.SUCCESS) data?.let { data ->
            if (data.isNotEmpty()) {
                handleMoviesUpdate(data)
                trailerViewPager.setVisible()
            } else {
                trailerViewPager.setGone()
            }
        } else if (status == Status.ERROR) {
            if (data != null && !data.isEmpty()) {
                handleMoviesUpdate(data)
                trailerViewPager.setVisible()
                view?.let {
                    Snackbar.make(it, getString(R.string.couldnt_update_data), 6000)
                        .show()
                }
            } else {
                trailerViewPager.setGone()
                errorMessage.setVisible()
            }
        }
    }

    private fun handleMoviesUpdate(data: List<Movie>) {
        val featuredTrailers = data.filter { it.trailerUrl != null }.subList(0, 6)
        viewPagerAdapter.fragmentList = featuredTrailers.map { TrailerFragment.create(it) }
        viewPagerAdapter.notifyDataSetChanged()
        trailerViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                pageIndicatorView.selection = position
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageScrollStateChanged(state: Int) {}

        })
        pageIndicatorView.count = featuredTrailers.size
    }

    override fun onResume() {
        super.onResume()
        if ((activity as MainActivity).isSelectedFragment(FRAG_ID)) context?.let {
            FirebaseAnalytics.getInstance(it)
                .setCurrentScreen(activity as Activity, "Home page", "Home page")
        }
    }

    override fun scrollToTop() {
        homeScrollView?.scrollTo(0, 0)
    }

    companion object {
        const val FRAG_ID = 0
    }
}