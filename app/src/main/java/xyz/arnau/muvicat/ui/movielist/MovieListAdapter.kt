package xyz.arnau.muvicat.ui.movielist

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import timber.log.Timber
import xyz.arnau.muvicat.GlideApp
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.ui.MoviePosterView
import javax.inject.Inject

class MovieListAdapter @Inject constructor() : RecyclerView.Adapter<MovieListAdapter.ViewHolder>() {
    var movies: List<Movie> = arrayListOf()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        holder.titleText.text = movie.title

        GlideApp.with(holder.itemView.context)
                .load("http://www.gencat.cat/llengua/cinema/${movie.posterUrl}")
                .placeholder(R.drawable.poster_placeholder)
                .centerCrop()
                .into(holder.posterImage)

        holder.itemView.setOnClickListener { Timber.d("You selected \"${movie.title}\"") }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.movie_card, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var posterImage: ImageView = view.findViewById(R.id.moviePoster)
        var titleText: TextView = view.findViewById(R.id.movieTitle)
    }
}
