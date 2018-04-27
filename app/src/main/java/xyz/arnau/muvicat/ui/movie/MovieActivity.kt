package xyz.arnau.muvicat.ui.movie

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.movie_info.*
import timber.log.Timber
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.model.Status
import xyz.arnau.muvicat.viewmodel.movie.MovieViewModel
import javax.inject.Inject
import android.support.v4.content.ContextCompat
import android.support.design.widget.AppBarLayout
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import xyz.arnau.muvicat.GlideApp


class MovieActivity : AppCompatActivity() {
    @Inject
    lateinit var movieViewModel: MovieViewModel

    @Inject
    lateinit var context: Context

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
    }

    private fun setupToolbar(movie: Movie?) {
        setSupportActionBar(movieInfoToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        movieInfoToolbarCollapsing
            .setCollapsedTitleTypeface(ResourcesCompat.getFont(context, R.font.nunito_sans_extrabold))

        movieInfoToolbarLayout.addOnOffsetChangedListener (object : AppBarLayout.OnOffsetChangedListener {
            var isShown = true
            var scrollRange = -1
            @SuppressLint("PrivateResource")
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                val backArrow =
                    ResourcesCompat.getDrawable(resources, R.drawable.abc_ic_ab_back_material, null)
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

    override fun onStart() {
        super.onStart()
        movieViewModel.movie.observe(this,
            Observer<Resource<Movie>> {
                if (it != null) handleDataState(it)
            })
    }

    private fun handleDataState(movieRes: Resource<Movie>) {
        when (movieRes.status) {
            Status.SUCCESS -> {
                Timber.i(movieRes.data.toString())
                setupToolbar(movieRes.data)

                GlideApp.with(context)
                    .load("http://www.gencat.cat/llengua/cinema/${movieRes.data?.posterUrl}")
                    .error(R.drawable.poster_placeholder)
                    .centerCrop()
                    .into(moviePoster)
                movieTitle.text = movieRes.data?.title
                if (movieRes.data?.ageRating != null) {
                    movieAgeRating.text = movieRes.data?.ageRating
                } else {
                    // movieAgeRating.visibility = View.GONE
                }
            }
            Status.ERROR -> throw Exception("The movie could not be retrieved")
            Status.LOADING -> return
        }
    }

    companion object {
        private const val MOVIE_ID = "movie_id"

        fun createIntent(context: Context, movieId: Long): Intent {
            return Intent(context, MovieActivity::class.java).putExtra(MOVIE_ID, movieId)
        }
    }
}