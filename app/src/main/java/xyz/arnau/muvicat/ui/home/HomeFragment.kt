package xyz.arnau.muvicat.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.ViewSkeletonScreen
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.home_fragment.*
import org.joda.time.LocalDate
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.di.Injectable
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.repository.model.Resource
import xyz.arnau.muvicat.repository.model.Showing
import xyz.arnau.muvicat.repository.model.Status
import xyz.arnau.muvicat.ui.LocationAwareActivity
import xyz.arnau.muvicat.ui.MainActivity
import xyz.arnau.muvicat.ui.UiPreferencesHelper
import xyz.arnau.muvicat.ui.utils.ScrollableToTop
import xyz.arnau.muvicat.ui.utils.ViewPagerAdapter
import xyz.arnau.muvicat.utils.AppExecutors
import xyz.arnau.muvicat.utils.LocationUtils
import xyz.arnau.muvicat.utils.setGone
import xyz.arnau.muvicat.utils.setVisible
import xyz.arnau.muvicat.viewmodel.movie.MovieListViewModel
import xyz.arnau.muvicat.viewmodel.showing.ShowingListViewModel
import java.util.*
import javax.inject.Inject

class HomeFragment : Fragment(), Injectable, ScrollableToTop, Filter.FilterListener {
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    @Inject
    lateinit var featuredMoviesAdapter: MovieListAdapter
    @Inject
    lateinit var appExecutors: AppExecutors
    private lateinit var featuredMoviesSkeleton: ViewSkeletonScreen
    private lateinit var newMoviesSkeleton: ViewSkeletonScreen
    private lateinit var nearbyShowingsSkeleton: ViewSkeletonScreen

    @Inject
    lateinit var newMoviesAdapter: MovieListAdapter
    @Inject
    lateinit var nearbyShowingsAdapter: NearbyShowingsAdapter

    @Inject
    lateinit var moviesViewModel: MovieListViewModel
    @Inject
    lateinit var showingsViewModel: ShowingListViewModel
    @Inject
    lateinit var preferencesHelper: UiPreferencesHelper

    private var hasLocation = false
    private var trailerTimer = Timer()
    internal var nearbyDistance = 0
        @SuppressLint("SetTextI18n")
        set(value) {
            preferencesHelper.nearbyShowingsDistance = value
            nearbyShowingsDistanceValue.text = "≈ $value km"
            nearbyShowingsAdapter.filter.filter(value.toString())
            field = value
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewPagerAdapter = ViewPagerAdapter(listOf(), childFragmentManager)
        trailerViewPager.adapter = viewPagerAdapter

        infoButton.setOnClickListener {
            startActivity(Intent(activity, AboutActivity::class.java))
        }
    }

    private fun setupTrailersTimer() {
        trailerTimer.schedule(object: TimerTask() {
            override fun run() {
                appExecutors.mainThread().execute {
                    if (trailerViewPager?.currentItem == viewPagerAdapter.count - 1) {
                        trailerViewPager?.currentItem = 0
                    } else {
                        trailerViewPager?.let { it.currentItem += 1 }
                    }
                }
            }
        }, 6000)
    }

    override fun onStart() {
        super.onStart()

        setupFeaturedMovies()
        setupNewMovies()
        setupNearbyShowings()

        trailerViewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                trailerTimer.cancel()
                trailerTimer = Timer()
                setupTrailersTimer()
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                print('t')
            }

            override fun onPageScrollStateChanged(state: Int) {
                print('t')
            }

        })

        nearbyDistance = preferencesHelper.nearbyShowingsDistance

        moviesViewModel.movies.observe(this,
            Observer<Resource<List<Movie>>> {
                if (it != null) handleMovieDataState(it.status, it.data)
            })

        showingsViewModel.showings.observe(this,
            Observer<Resource<List<Showing>>> {
                if (it != null) handleShowingDataState(it.status, it.data)
            })
    }

    override fun onResume() {
        super.onResume()
        if (!hasLocation && getLastLocation() != null) {
            notifyLastLocation(getLastLocation()!!)
        }
        if ((activity as MainActivity).isSelectedFragment(FRAG_ID)) context?.let {
            FirebaseAnalytics.getInstance(it)
                .setCurrentScreen(activity as Activity, "Home page", "Home page")
        }
    }

    private fun getLastLocation() = (activity as LocationAwareActivity).lastLocation

    fun notifyLastLocation(lastLocation: Location) {
        if (::nearbyShowingsAdapter.isInitialized) {
            updateNearbyShowings(nearbyShowingsAdapter.showings, lastLocation)
        } else {
            hasLocation = false
        }
    }

    private fun setLocationToShowings(showingList: List<Showing>, location: Location) {
        showingList.forEach {
            if (it.cinemaLatitude != null && it.cinemaLongitude != null) {
                it.cinemaDistance = LocationUtils.getDistance(
                    location,
                    it.cinemaLatitude!!,
                    it.cinemaLongitude!!
                )
            }
        }
    }

    private fun updateNearbyShowings(data: List<Showing>, lastLocation: Location?) {
        if (lastLocation != null) {
            setLocationToShowings(data, lastLocation)
            hasLocation = true
            nearbyShowingsAdapter.showings = data
            nearbyShowingsAdapter.filter.filter(nearbyDistance.toString())
            nearbyShowingsRecyclerView.setVisible()
            nearbyShowingsAdapter.notifyDataSetChanged()
        } else {
            nearbyShowingsRecyclerView.setGone()
        }
    }

    private fun setupFeaturedMovies() {
        featuredMoviesRecyclerView.adapter = featuredMoviesAdapter

        featuredMoviesSkeleton = Skeleton.bind(featuredMoviesRecyclerView as View)
            .color(R.color.skeleton_shimmer)
            .load(R.layout.movie_list_horiz_skeleton)
            .show()
        featuredMoviesRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupNewMovies() {
        newMoviesRecyclerView.adapter = newMoviesAdapter

        newMoviesSkeleton = Skeleton.bind(newMoviesRecyclerView as View)
            .color(R.color.skeleton_shimmer)
            .load(R.layout.movie_list_horiz_skeleton)
            .show()
        newMoviesRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    @SuppressLint("InflateParams")
    private fun setupNearbyShowings() {
        nearbyShowingsRecyclerView.adapter = nearbyShowingsAdapter
        nearbyShowingsAdapter.filterListener = this

        nearbyShowingsSkeleton = Skeleton.bind(nearbyShowingsRecyclerView as View)
            .color(R.color.skeleton_shimmer)
            .load(R.layout.movie_list_horiz_skeleton)
            .show()
        nearbyShowingsRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        nearbyShowingsDistance.setOnClickListener {
            val distanceDialog = DistanceDialog(this)
            distanceDialog.setContentView(
                layoutInflater.inflate(R.layout.distance_dialog, null)
            )
            distanceDialog.show()
        }
    }

    private fun handleMovieDataState(status: Status, data: List<Movie>?) {
        errorMessage.setGone()
        if (status == Status.SUCCESS) data?.let { d ->
            if (d.isNotEmpty()) {
                handleMoviesUpdate(d)
                featuredMoviesSkeleton.hide()
                newMoviesSkeleton.hide()
                trailerViewPager.setVisible()
                setupTrailersTimer()
            } else {
                trailerViewPager.setGone()
            }
        } else if (status == Status.ERROR) {
            if (data != null && !data.isEmpty()) {
                handleMoviesUpdate(data)
                featuredMoviesSkeleton.hide()
                newMoviesSkeleton.hide()
                trailerViewPager.setVisible()
                setupTrailersTimer()
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

    private fun handleShowingDataState(status: Status, data: List<Showing>?) {
        errorMessage.setGone()
        if (status == Status.SUCCESS) data?.let { d ->
            if (d.isNotEmpty()) {
                updateNearbyShowings(d, getLastLocation())
                nearbyShowingsRecyclerView.setVisible()
                nearbyShowingsSkeleton.hide()
            } else {
                nearbyShowingsRecyclerView.setGone()
            }
        } else if (status == Status.ERROR) {
            if (data != null && !data.isEmpty()) {
                updateNearbyShowings(data, getLastLocation())
                nearbyShowingsSkeleton.hide()
            } else {
                errorMessage.setVisible()
            }
        }
    }

    private fun handleMoviesUpdate(data: List<Movie>) {
        var featuredTrailers = data.filter { it.trailerUrl != null }
        if (featuredTrailers.size > 6)
            featuredTrailers = featuredTrailers.subList(0, 6)
        val featuredMovies = if (data.size > 6) data.subList(0, 8) else data
        var newMovies =
            data.filter {
                it.releaseDate != null && !LocalDate(it.releaseDate!!).isBefore(
                    LocalDate.now().minusDays(
                        5
                    )
                )
            }
                .sortedBy { it.releaseDate!!.time }
        if (newMovies.size > 8)
            newMovies = newMovies.subList(0, 8)

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
        pageIndicatorView.setVisible()

        updateFeaturedMovies(featuredMovies)
        updateNewMovies(newMovies)
    }

    override fun onFilterComplete(count: Int) {
        if (count == 0) {
            nearbyShowingsRecyclerView.setGone()
            nearbyShowingsEmptyMessage.setVisible()
        } else {
            nearbyShowingsRecyclerView.setVisible()
            nearbyShowingsEmptyMessage.setGone()
        }
    }

    private fun updateFeaturedMovies(featuredMovies: List<Movie>) {
        featuredMoviesAdapter.movies = featuredMovies
        featuredMoviesAdapter.showReleaseDate = true
        featuredMoviesAdapter.notifyDataSetChanged()
    }

    private fun updateNewMovies(newMovies: List<Movie>) {
        newMoviesAdapter.movies = newMovies
        newMoviesAdapter.showReleaseDate = true
        newMoviesAdapter.notifyDataSetChanged()
    }

    override fun scrollToTop() {
        homeScrollView?.scrollTo(0, 0)
    }

    companion object {
        const val FRAG_ID = 0
    }
}