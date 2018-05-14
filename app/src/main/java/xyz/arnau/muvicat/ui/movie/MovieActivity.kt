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
import android.view.View
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.movie_info.*
import kotlinx.android.synthetic.main.showing_list_toolbar.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.MovieShowing
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.model.Status
import xyz.arnau.muvicat.ui.LocationAwareActivity
import xyz.arnau.muvicat.ui.SimpleDividerItemDecoration
import xyz.arnau.muvicat.ui.showing.MovieShowingsAdapter
import xyz.arnau.muvicat.utils.DateFormatter
import xyz.arnau.muvicat.utils.GlideApp
import xyz.arnau.muvicat.utils.LocationUtils
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
    lateinit var showingsAdapter: MovieShowingsAdapter

    private var hasLocation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.movie_info)
        setupToolbar(null)

        val movieId = intent.getLongExtra(MOVIE_ID, (-1).toLong())
        if (movieId == (-1).toLong())
            throw Exception("Missing movie identifier")
        else {
            movieViewModel.setId(movieId)
        }
        moviesShowingsRecyclerView.adapter = showingsAdapter
        moviesShowingsRecyclerView.layoutManager = LinearLayoutManager(this)
        moviesShowingsRecyclerView.addItemDecoration(SimpleDividerItemDecoration(context!!))

        movieInfoToolbar.setOnClickListener {
            movieInfoToolbarLayout.setExpanded(true)
            nestedScrollView.scrollY = 0
        }
    }

    override fun onStart() {
        super.onStart()

        movieViewModel.movie
            .observe(this, Observer { if (it != null) handleMovieDataState(it) })
        movieViewModel.showings
            .observe(this, Observer { if (it != null) handleShowingsDateState(it.status, it.data) })
    }

    private fun handleMovieDataState(movieRes: Resource<Movie>) {
        when (movieRes.status) {
            Status.SUCCESS -> {
                val movie = movieRes.data
                if (movie != null) {
                    setupToolbar(movie)

                    GlideApp.with(context)
                        .load("http://www.gencat.cat/llengua/cinema/${movieRes.data.posterUrl}")
                        .error(R.drawable.poster_placeholder)
                        .centerCrop()
                        .into(moviePoster)
                    movieTitle.text = movie.title

                    if (movie.ageRating != null && movie.year != null)
                        movieYearAgeRatingSeparator.visibility = View.VISIBLE

                    if (movie.ageRating != null) {
                        movieAgeRating.text = movie.ageRating
                        movieAgeRating.visibility = View.VISIBLE
                    }

                    if (movie.year != null) {
                        movieYear.text = movie.year.toString()
                        movieYear.visibility = View.VISIBLE
                    }

                    if (movie.plot != null) {
                        moviePlot.text = movie.plot
                        moviePlot.visibility = View.VISIBLE
                    }

                    if (movie.originalTitle != null) {
                        movieOriginalTitle.text = movie.originalTitle
                        movieOriginalTitleLayout.visibility = View.VISIBLE
                    }

                    if (movie.direction != null) {
                        movieDirection.text = movie.direction
                        movieDirectionLayout.visibility = View.VISIBLE
                    }

                    if (movie.releaseDate != null) {
                        movieReleaseDate.text = dateFormatter.longDate(movie.releaseDate!!)
                        movieReleaseDateLayout.visibility = View.VISIBLE
                    }

                    if (movie.originalLanguage != null) {
                        movieOriginalLanguage.text = movie.originalLanguage
                        movieOriginalLanguageLayout.visibility = View.VISIBLE
                    }

                    if (movie.cast != null) {
                        movieCast.text = movie.cast
                        movieCastLayout.visibility = View.VISIBLE
                    }
                }
            }
            Status.ERROR -> throw Exception("The movie could not be retrieved")
            Status.LOADING -> return
        }
    }

    private fun handleShowingsDateState(status: Status, showings: List<MovieShowing>?) {
        if (status == Status.SUCCESS) showings?.let {
            updateShowingsList(it, lastLocation)
            movieShowingsList.visibility = View.VISIBLE
        } else if (status == Status.ERROR) {
            if (showings != null && !showings.isEmpty()) {
                updateShowingsList(showings, lastLocation)
                movieShowingsList.visibility = View.VISIBLE
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
        showingsAdapter.showings =
                showings.sortedWith(
                    compareBy<MovieShowing> { it.date }.thenBy(
                        nullsLast(),
                        { it.cinemaDistance })
                )
        showingsAdapter.notifyDataSetChanged()
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
        if (::showingsAdapter.isInitialized) {
            updateShowingsList(showingsAdapter.showings, lastLocation)
        } else {
            hasLocation = false
        }
    }

    companion object {
        private const val MOVIE_ID = "movie_id"

        fun createIntent(context: Context, movieId: Long): Intent {
            return Intent(context, MovieActivity::class.java).putExtra(MOVIE_ID, movieId)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}