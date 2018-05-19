package xyz.arnau.muvicat.ui.movie

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Location
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.movie_info.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.repository.model.*
import xyz.arnau.muvicat.ui.LocationAwareActivity
import xyz.arnau.muvicat.ui.SimpleDividerItemDecoration
import xyz.arnau.muvicat.ui.showing.MovieShowingsAdapter
import xyz.arnau.muvicat.utils.*
import xyz.arnau.muvicat.viewmodel.movie.MovieViewModel
import javax.inject.Inject


class MovieActivity : LocationAwareActivity() {
    @Inject
    lateinit var movieViewModel: MovieViewModel
    @Inject
    lateinit var dateFormatter: DateFormatter
    @Inject
    lateinit var context: Context
    @Inject
    lateinit var infoAndShowingsAdapter: MovieShowingsAdapter

    private var hasLocation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.movie_info)
        setupToolbar(null)

        val movieId = intent.getLongExtra(MOVIE_ID, (-1).toLong())
        if (movieId == (-1).toLong())
            throw Exception("Missing movie identifier")
        else
            movieViewModel.setId(movieId)

        val showingId = intent.getLongExtra(SHOWING_ID, (-1).toLong())
        if (showingId != (-1).toLong()) {
            infoAndShowingsAdapter.showingId = showingId
            infoAndShowingsAdapter.expanded = false
        }

        movieInfoAndShowingsRecyclerView.adapter = infoAndShowingsAdapter
        movieInfoAndShowingsRecyclerView.layoutManager = LinearLayoutManager(this)
        movieInfoAndShowingsRecyclerView.addItemDecoration(SimpleDividerItemDecoration(context, 1))

        movieInfoToolbar.setOnClickListener {
            movieInfoToolbarLayout.setExpanded(true)
            movieInfoAndShowingsRecyclerView.scrollToPosition(0)
        }
    }

    override fun onStart() {
        super.onStart()

        movieViewModel.movie
            .observe(this, Observer { if (it != null) handleMovieDataState(it) })
        movieViewModel.showings
            .observe(this, Observer { if (it != null) handleShowingsDateState(it.status, it.data) })
    }

    private fun handleMovieDataState(movieRes: Resource<MovieWithCast>) {
        when (movieRes.status) {
            Status.SUCCESS -> {
                val movieWithCast = movieRes.data
                if (movieWithCast != null) {
                    val movie = movieWithCast.movie
                    setupToolbar(movieWithCast.movie)

                    GlideApp.with(context)
                        .load("http://www.gencat.cat/llengua/cinema/${movie.posterUrl}")
                        .error(R.drawable.poster_placeholder)
                        .centerCrop()
                        .into(moviePoster)
                    movieTitle.text = movie.title

                    if (movie.ageRating != null && movie.year != null)
                        movieYearAgeRatingSeparator.setVisible()

                    movieAgeRating.setVisibleText(movie.ageRating)
                    movieYear.setVisibleText(movie.year?.toString())

                    infoAndShowingsAdapter.movie = movie
                    infoAndShowingsAdapter.notifyDataSetChanged()
                }
            }
            Status.ERROR -> throw Exception("The movie could not be retrieved")
            Status.LOADING -> return
        }
    }

    private fun handleShowingsDateState(status: Status, showings: List<MovieShowing>?) {
        if (status == Status.SUCCESS) showings?.let {
            updateShowingsList(it, lastLocation)
        } else if (status == Status.ERROR) {
            if (showings != null && !showings.isEmpty()) {
                updateShowingsList(showings, lastLocation)
                Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.couldnt_update_data),
                    6000
                )
                    .show()
            }
        }
    }

    private fun setupToolbar(movie: Movie?) {
        setSupportActionBar(movieInfoToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        movieInfoToolbarCollapsing
            .setCollapsedTitleTypeface(
                ResourcesCompat.getFont(
                    context,
                    R.font.nunito_sans_extrabold
                )
            )
        movieInfoToolbar.setNavigationOnClickListener { onBackPressed() }


        movieInfoToolbarLayout.addOnOffsetChangedListener(object :
            AppBarLayout.OnOffsetChangedListener {
            var isShown = true
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                val backArrow =
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_chevron_left_black, null)
                if (verticalOffset < -300) {
                    backArrow?.setColorFilter(Color.parseColor("#AF0000"), PorterDuff.Mode.SRC_ATOP)
                    supportActionBar!!.setHomeAsUpIndicator(backArrow)
                    movieInfoToolbarCollapsing.title = "Title"
                } else {
                    backArrow?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
                    supportActionBar!!.setHomeAsUpIndicator(backArrow)
                }

                if (scrollRange == -1) {
                    scrollRange = movieInfoToolbarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    movieInfoToolbarCollapsing.title = movie?.title
                    isShown = true
                } else {
                    movieInfoToolbarCollapsing.title = " "
                    isShown = false
                }
            }
        })
    }

    private fun updateShowingsList(showings: List<MovieShowing>, lastLocation: Location?) {
        if (lastLocation != null) {
            setLocationToShowings(showings, lastLocation)
            hasLocation = true
        }
        infoAndShowingsAdapter.showings =
                showings.sortedWith(
                    compareBy<MovieShowing> { it.date }.thenBy(
                        nullsLast(),
                        { it.cinemaDistance })
                )
        infoAndShowingsAdapter.notifyDataSetChanged()
    }

    private fun setLocationToShowings(showings: List<MovieShowing>, location: Location) {
        showings.forEach {
            if (it.cinemaLatitude != null && it.cinemaLongitude != null) {
                it.cinemaDistance = LocationUtils.getDistance(
                    location,
                    it.cinemaLatitude!!,
                    it.cinemaLongitude!!
                )
            }
        }
    }

    override fun processLastLocation(location: Location) {
        if (::infoAndShowingsAdapter.isInitialized) {
            updateShowingsList(infoAndShowingsAdapter.showings, lastLocation)
        } else {
            hasLocation = false
        }
    }

    companion object {
        private const val MOVIE_ID = "movie_id"
        private const val SHOWING_ID = "showing_id"

        fun createIntent(context: Context, movieId: Long, showingId: Long? = null): Intent {
            return Intent(context, MovieActivity::class.java).apply {
                putExtra(MOVIE_ID, movieId)
                showingId?.let { putExtra(SHOWING_ID, showingId) }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
    }
}