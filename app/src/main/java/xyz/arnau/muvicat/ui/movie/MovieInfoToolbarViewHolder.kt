package xyz.arnau.muvicat.ui.movie

import android.annotation.SuppressLint
import android.content.Context
import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.Rating
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.youtube.player.YouTubeIntents
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.ui.utils.toString1Decimal
import xyz.arnau.muvicat.utils.GlideApp
import xyz.arnau.muvicat.utils.setGone
import xyz.arnau.muvicat.utils.setVisible
import xyz.arnau.muvicat.utils.setVisibleText
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import javax.inject.Inject

class MovieInfoToolbarViewHolder(private val activity: MovieActivity) {
    private val title = activity.findViewById<TextView>(R.id.movieTitle)
    private val poster = activity.findViewById<ImageView>(R.id.moviePoster)
    private val year = activity.findViewById<TextView>(R.id.movieYear)
    private val genres = activity.findViewById<TextView>(R.id.movieGenre)
    private val yearGenreSeparator = activity.findViewById<View>(R.id.movieYearGenreSeparator)
    private val runtime = activity.findViewById<TextView>(R.id.movieRuntime)
    private val ageRating = activity.findViewById<TextView>(R.id.movieAgeRating)
    private val runtimeAgeRatingSeparator =
        activity.findViewById<View>(R.id.movieRuntimeAgeRatingSeparator)
    private val ratingLayout = activity.findViewById<View>(R.id.ratingLayout)
    private val tmdbRatingLayout = activity.findViewById<View>(R.id.tmdbRatingLayout)
    private val tmdbNoRating = activity.findViewById<TextView>(R.id.tmdbNoRatingText)
    private val tmdbAverageRating = activity.findViewById<TextView>(R.id.movieTmdbVoteAverage)
    private val tmdbRatingCount = activity.findViewById<TextView>(R.id.movieTmdbVoteCount)
    private val tmdbRatingBar = activity.findViewById<MaterialRatingBar>(R.id.movieTmdbVoteStars)
    private val rateMovieButton = activity.findViewById<Button>(R.id.rateMovieButton)
    private val userRatingLayout = activity.findViewById<View>(R.id.userRatingLayout)
    private val userRating = activity.findViewById<TextView>(R.id.movieUserRating)
    private val userRatingBar = activity.findViewById<MaterialRatingBar>(R.id.movieUserVoteStars)
    private val playTrailerButton = activity.findViewById<ImageView>(R.id.playTrailer)
    private val backdrop = activity.findViewById<ImageView>(R.id.movieBackdrop)

    fun setupMovie(movie: Movie) {
        setupBasicInfo(movie)
        setupExtraInfo(movie)
    }

    private fun setupBasicInfo(movie: Movie) {
        title.text = movie.title
        year.setVisibleText(movie.year?.toString())
        ageRating.setVisibleText(movie.ageRating)

        movie.posterUrl?.let {
            GlideApp.with(activity)
                .load("http://www.gencat.cat/llengua/cinema/$it")
                .centerCrop()
                .into(poster)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupExtraInfo(movie: Movie) {
        movie.tmdbId?.let {
            setupRating(movie)

            movie.runtime?.let {
                runtime.setVisibleText(parseRuntime(it))
                movie.ageRating?.let { runtimeAgeRatingSeparator.setVisible() }
            }

            movie.genres?.let {
                genres.setVisibleText(parseGenres(it))
                movie.year?.let { yearGenreSeparator.setVisible() }
            }

            movie.backdropUrl?.let {
                GlideApp.with(activity)
                    .load("https://image.tmdb.org/t/p/w1280$it")
                    .centerCrop()
                    .into(backdrop)
            }
        }

        movie.trailerUrl?.let { videoId ->
            playTrailerButton.setVisible()
            playTrailerButton.setOnClickListener { watchYoutubeVideo(activity, videoId) }

            if (movie.backdropUrl == null) {
                GlideApp.with(activity)
                    .load("https://img.youtube.com/vi/$videoId/maxresdefault.jpg")
                    .centerCrop()
                    .into(backdrop)
            }
        }
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun setupRating(movie: Movie) {
        movie.voteCount?.let { ratingCount ->
            movie.voteAverage?.let { averageRating ->
                ratingLayout.setVisible()
                if (movie.voteCount == 0)
                    tmdbNoRating.setVisible()
                else {
                    tmdbRatingLayout.setVisible()
                    tmdbRatingBar.rating = averageRating.div(2).toFloat()
                    tmdbRatingCount.text = parseRatingCount(ratingCount)
                    tmdbAverageRating.text = "${averageRating.div(2).toString1Decimal()}/5"
                }

                if (movie.vote == null) {
                    userRatingLayout.setGone()
                    rateMovieButton.setVisible()
                    rateMovieButton.setOnClickListener {
                        val ratingDialog = RatingDialog(activity, movie)
                        ratingDialog.setContentView(
                            activity.layoutInflater.inflate(
                                R.layout.rating_dialog,
                                null
                            )
                        )
                        ratingDialog.show()
                    }
                }
                movie.vote?.let {
                    rateMovieButton.setGone()
                    userRatingLayout.setVisible()
                    userRatingLayout.setOnClickListener {
                        val ratingDialog = RatingDialog(activity, movie)
                        ratingDialog.setContentView(
                            activity.layoutInflater.inflate(
                                R.layout.rating_dialog,
                                null
                            )
                        )
                        ratingDialog.show()
                    }
                    userRating.text = "${it.div(2).toString1Decimal()}/5"
                    userRatingBar.rating = it.div(2).toFloat()
                }
            }
        }
    }

    private fun parseRatingCount(count: Int): String {
        return when {
            count == 1 -> return "1 ${activity.getString(R.string.vote)}"
            count < 1000 -> "$count ${activity.getString(R.string.votes)}"
            count < 1000000 -> "${((count / 100).toDouble() / 10).toString1Decimal()}m ${activity.getString(
                R.string.votes
            )}"
            else -> "${(count / 100000).toDouble() / 10}M ${activity.getString(R.string.votes)}"
        }
    }

    private fun parseGenres(genres: List<String>?): String? {
        if (genres == null)
            return null

        return genres.toString().dropLast(1).drop(1)
    }

    private fun parseRuntime(runtime: Int?): String? {
        if (runtime == null)
            return null

        return when {
            runtime < 60 -> "$runtime min"
            runtime % 60 == 0 -> "${runtime / 60} h"
            else -> "${runtime / 60} h ${runtime % 60} min"
        }
    }

    private fun watchYoutubeVideo(context: Context, id: String) {
        try {
            context.startActivity(
                YouTubeIntents.createPlayVideoIntentWithOptions(activity, id, true, true)
            )
        } catch (ex: ActivityNotFoundException) {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$id"))
            )
        }

    }
}
