package xyz.arnau.muvicat.ui.home

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.ui.movie.MovieActivity
import xyz.arnau.muvicat.utils.*
import javax.inject.Inject

class MovieListAdapter @Inject constructor() : RecyclerView.Adapter<MovieListAdapter.ViewHolder>() {
    @Inject
    lateinit var dateFormatter: DateFormatter

    @Inject
    lateinit var context: Context

    var movies: List<Movie> = listOf()
    var showReleaseDate: Boolean = false

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        holder.titleText.text = movie.title
        if (showReleaseDate)
            holder.releaseDate.setVisibleText(dateFormatter.shortReleaseDate(movie.releaseDate))
        if (movie.vote !== null) {
            holder.star.setVisible()
        } else {
            holder.star.setGone()
        }

        GlideApp.with(holder.itemView.context)
            .load("http://www.gencat.cat/llengua/cinema/${movie.posterUrl}")
            .error(R.drawable.poster_placeholder)
            .centerCrop()
            .into(holder.posterImage)

        holder.itemView.setOnClickListener {
            context.startActivity(
                MovieActivity.createIntent(context, movie.id)
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_item_horiz, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var posterImage: ImageView = view.findViewById(R.id.moviePoster)
        var titleText: TextView = view.findViewById(R.id.movieTitle)
        var releaseDate: TextView = view.findViewById(R.id.releaseDate)
        var star: ImageView = view.findViewById(R.id.star)
    }
}
