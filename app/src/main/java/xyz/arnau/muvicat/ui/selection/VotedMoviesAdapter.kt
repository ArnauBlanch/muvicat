package xyz.arnau.muvicat.ui.selection

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import org.apache.commons.lang3.StringUtils
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.ui.movie.MovieActivity
import xyz.arnau.muvicat.ui.utils.UiUtils
import xyz.arnau.muvicat.ui.utils.toString1Decimal
import xyz.arnau.muvicat.utils.*
import javax.inject.Inject

class VotedMoviesAdapter @Inject constructor() :
    RecyclerView.Adapter<VotedMoviesAdapter.ViewHolder>() {
    @Inject
    lateinit var dateFormatter: DateFormatter

    @Inject
    lateinit var context: Context

    var movies: List<Movie> = listOf()

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        holder.titleText.text = movie.title
        GlideApp.with(holder.itemView.context)
            .load("http://www.gencat.cat/llengua/cinema/${movie.posterUrl}")
            .error(R.drawable.poster_placeholder)
            .centerCrop()
            .into(holder.posterImage)
        movie.vote?.let { rating ->
            //holder.rating.text = "${rating.div(2).toString1Decimal()}/5"
            holder.ratingBar.rating = rating.toFloat().div(2)
        }

        holder.itemView.setOnClickListener {
            context.startActivity(
                MovieActivity.createIntent(context, movie.id)
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.voted_movie_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = movies.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var posterImage: ImageView = view.findViewById(R.id.moviePoster)
        var titleText: TextView = view.findViewById(R.id.movieTitle)
        var ratingBar: MaterialRatingBar = view.findViewById(R.id.movieUserVoteStars)
        //var rating: TextView = view.findViewById(R.id.movieUserRating)
    }
}
