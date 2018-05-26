package xyz.arnau.muvicat.ui.movie

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.movie_info.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.repository.model.*
import xyz.arnau.muvicat.ui.LocationAwareActivity
import xyz.arnau.muvicat.ui.ScrollableToTop
import xyz.arnau.muvicat.ui.SimpleDividerItemDecoration
import xyz.arnau.muvicat.ui.ToolbarUtils
import xyz.arnau.muvicat.utils.DateFormatter
import xyz.arnau.muvicat.utils.LocationUtils
import xyz.arnau.muvicat.viewmodel.movie.MovieViewModel
import javax.inject.Inject


class MovieActivity : LocationAwareActivity(), ScrollableToTop {
    @Inject
    lateinit var movieViewModel: MovieViewModel
    @Inject
    lateinit var dateFormatter: DateFormatter
    @Inject
    lateinit var context: Context
    @Inject
    lateinit var infoAndShowingsAdapter: MovieShowingsAdapter

    private var hasLocation = false
    private lateinit var toolbarViewHolder: MovieInfoToolbarViewHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.movie_info)

        setupToolbar(null)
        processIntentIds()
        setupRecyclerView()
        toolbarViewHolder = MovieInfoToolbarViewHolder(this)
    }

    private fun processIntentIds() {
        val movieId = intent.getLongExtra(MOVIE_ID, (-1).toLong())
        if (movieId == (-1).toLong())
            throw Exception("Missing movie identifier")
        else
            movieViewModel.setId(movieId)

        val showingId = intent.getLongExtra(SHOWING_ID, (-1).toLong())
        val cinemaId = intent.getLongExtra(CINEMA_ID, (-1).toLong())
        if (showingId != (-1).toLong()) {
            infoAndShowingsAdapter.showingId = showingId
            infoAndShowingsAdapter.expanded = false
        } else if (cinemaId != (-1).toLong()) {
            infoAndShowingsAdapter.cinemaId = cinemaId
            infoAndShowingsAdapter.expanded = false
        }
    }

    private fun setupToolbar(title: String?) {
        ToolbarUtils.setupCollapsingToolbar(
            this,
            R.id.movieInfoToolbar,
            R.id.movieInfoToolbarLayout,
            R.id.movieInfoToolbarCollapsing,
            title,
            this
        )
    }

    private fun setupRecyclerView() {
        movieInfoAndShowingsRecyclerView.adapter = infoAndShowingsAdapter
        movieInfoAndShowingsRecyclerView.layoutManager = LinearLayoutManager(this)
        movieInfoAndShowingsRecyclerView.addItemDecoration(SimpleDividerItemDecoration(context, 1))
    }

    override fun onStart() {
        super.onStart()

        movieViewModel.movie
            .observe(this, Observer { if (it != null) handleMovieDataState(it) })
        movieViewModel.showings
            .observe(this, Observer { if (it != null) handleShowingsDateState(it.status, it.data) })
    }

    private fun handleMovieDataState(movieRes: Resource<MovieWithCast>) {
        val movieWithCast = movieRes.data
        movieWithCast?.let {
            processMovie(it.movie, it.castMembers)
        }
        if (movieRes.status == Status.ERROR && movieRes.data == null)
            finish()
    }

    private fun processMovie(movie: Movie, castMembers: List<CastMember>) {
        setupToolbar(movie.title)

        infoAndShowingsAdapter.castMembers = castMembers.sortedBy { it.order }
        infoAndShowingsAdapter.movie = movie
        infoAndShowingsAdapter.notifyDataSetChanged()
        toolbarViewHolder.setupMovie(movie)
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

    private fun updateShowingsList(showings: List<MovieShowing>, lastLocation: Location?) {
        if (lastLocation != null) {
            setLocationToShowings(showings, lastLocation)
            hasLocation = true
        }
        infoAndShowingsAdapter.showings =
                showings.sortedWith(
                    compareBy<MovieShowing> { it.date }.thenBy(nullsLast(), { it.cinemaDistance })
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

    override fun scrollToTop() {
        movieInfoAndShowingsRecyclerView.scrollToPosition(0)
    }

    fun rateMovie(tmdbId: Int, rating: Double) {
        movieViewModel.rateMovie(tmdbId, rating).observe(
            this, Observer {
                if (it != null) {
                    if (it.status == Status.SUCCESS) {
                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.rate_movie_success), 8000)
                            .show()
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.rate_movie_error), 8000)
                            .show()
                    }
                }
            }
        )
    }

    fun unrateMovie(tmdbId: Int) {
        movieViewModel.unrateMovie(tmdbId).observe(
            this, Observer {
                if (it != null) {
                    if (it.status == Status.SUCCESS) {
                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.unrate_movie_success), 8000)
                            .show()
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.unrate_movie_error), 8000)
                            .show()
                    }
                }
            }
        )
    }


    companion object {
        private const val MOVIE_ID = "movie_id"
        private const val SHOWING_ID = "showing_id"
        private const val CINEMA_ID = "cinema_id"

        fun createIntent(
            context: Context,
            movieId: Long,
            showingId: Long? = null,
            cinemaId: Long? = null
        ): Intent {
            return Intent(context, MovieActivity::class.java).apply {
                putExtra(MOVIE_ID, movieId)
                showingId?.let { putExtra(SHOWING_ID, showingId) }
                cinemaId?.let { putExtra(CINEMA_ID, cinemaId) }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
    }
}