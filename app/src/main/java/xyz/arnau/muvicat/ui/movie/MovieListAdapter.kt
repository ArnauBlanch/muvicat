package xyz.arnau.muvicat.ui.movie

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import org.apache.commons.lang3.StringUtils
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.utils.DateFormatter
import xyz.arnau.muvicat.utils.GlideApp
import xyz.arnau.muvicat.utils.setVisibleText
import javax.inject.Inject

class MovieListAdapter @Inject constructor() : RecyclerView.Adapter<MovieListAdapter.ViewHolder>(),
    Filterable {
    @Inject
    lateinit var dateFormatter: DateFormatter

    @Inject
    lateinit var context: Context

    var movies: List<Movie> = listOf()
        set(movies) {
            moviesFiltered = movies
            field = movies
        }

    private var moviesFiltered: List<Movie> = listOf()
    var cinemaId: Long? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = moviesFiltered[position]
        holder.titleText.text = movie.title
        holder.releaseDate.setVisibleText(dateFormatter.shortReleaseDate(movie.releaseDate))

        GlideApp.with(holder.itemView.context)
            .load("http://www.gencat.cat/llengua/cinema/${movie.posterUrl}")
            .error(R.drawable.poster_placeholder)
            .centerCrop()
            .into(holder.posterImage)

        holder.itemView.setOnClickListener {
            context.startActivity(
                MovieActivity.createIntent(
                    context,
                    movie.id,
                    cinemaId = cinemaId
                )
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return moviesFiltered.size
    }

    override fun getFilter(): Filter =
        object: Filter() {
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                moviesFiltered = results?.values as List<Movie>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString()
                var list = listOf<Movie>()
                if (charString.isEmpty()) {
                    list = movies
                } else {
                    movies.forEach {
                        val titleString = StringUtils.stripAccents(it.title?.toLowerCase())
                        val plotString = StringUtils.stripAccents(it.plot?.toLowerCase())
                        val searchStr = StringUtils.stripAccents(charString.toLowerCase())
                        if (titleString.contains(searchStr) || plotString.contains(searchStr)) {
                            list += it
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = list
                return filterResults
            }

        }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var posterImage: ImageView = view.findViewById(R.id.moviePoster)
        var titleText: TextView = view.findViewById(R.id.movieTitle)
        var releaseDate: TextView = view.findViewById(R.id.releaseDate)
    }
}
