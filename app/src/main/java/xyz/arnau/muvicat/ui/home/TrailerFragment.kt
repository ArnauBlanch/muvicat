package xyz.arnau.muvicat.ui.home

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.youtube.player.YouTubeIntents
import kotlinx.android.synthetic.main.trailer_item.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.di.Injectable
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.ui.movie.MovieActivity
import xyz.arnau.muvicat.utils.GlideApp
import xyz.arnau.muvicat.utils.setVisible
import javax.inject.Inject

class TrailerFragment : Fragment(), Injectable {
    @Inject
    lateinit var contextAux: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.trailer_item, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments
    }

    override fun onStart() {
        super.onStart()

        val id = arguments?.getLong(MOVIE_ID)
        val title = arguments?.getString(MOVIE_TITLE)
        val backdropUrl = arguments?.getString(MOVIE_BACKDROP_URL)
        val trailerUrl = arguments?.getString(MOVIE_TRAILER_URL)

        movieTitle.text = title
        backdropUrl?.let {
            GlideApp.with(this)
                .load("https://image.tmdb.org/t/p/w1280$it")
                .centerCrop()
                .into(movieBackdrop)
        }
        trailerUrl?.let { videoId ->
            playTrailer.setVisible()
            playTrailer.setOnClickListener { watchYoutubeVideo(contextAux, videoId) }

            if (backdropUrl == null) {
                GlideApp.with(this)
                    .load("https://img.youtube.com/vi/$videoId/maxresdefault.jpg")
                    .centerCrop()
                    .error(
                        Glide.with(this)
                            .load("https://img.youtube.com/vi/$videoId/sddefault.jpg")
                            .error(
                                GlideApp.with(this)
                                    .load("https://img.youtube.com/vi/$videoId/hqdefault.jpg")
                            )
                    )
                    .into(movieBackdrop)
            }
        }
        bottomLayout.setOnClickListener {
            contextAux.startActivity(
                MovieActivity.createIntent(contextAux, id!!)
            )
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

    companion object {
        private const val MOVIE_ID = "movie_id"
        private const val MOVIE_TITLE = "movie_title"
        private const val MOVIE_TRAILER_URL = "movie_trailer_url"
        private const val MOVIE_BACKDROP_URL = "movie_backdrop_url"

        fun create(movie: Movie): Fragment =
            TrailerFragment().apply {
                val bundle = Bundle().apply {
                    putLong(MOVIE_ID, movie.id)
                    putString(MOVIE_TITLE, movie.title)
                    putString(MOVIE_BACKDROP_URL, movie.backdropUrl)
                    putString(MOVIE_TRAILER_URL, movie.trailerUrl)
                }
                arguments = bundle
            }
    }
}