package xyz.arnau.muvicat.ui.home

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.repository.model.Showing
import xyz.arnau.muvicat.ui.movie.MovieActivity
import xyz.arnau.muvicat.utils.*
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class NearbyShowingsAdapter @Inject constructor() :
    RecyclerView.Adapter<NearbyShowingsAdapter.ViewHolder>(), Filterable {
    @Inject
    lateinit var dateFormatter: DateFormatter

    @Inject
    lateinit var context: Context

    var filterListener: Filter.FilterListener? = null
    var showings: List<Showing> = listOf()
        set(value) {
            field = value
            filteredShowings = showings
        }
    private var filteredShowings: List<Showing> = listOf()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val showing = filteredShowings[position]
        holder.titleText.text = showing.movieTitle
        if (showing.movieVoted) {
            holder.star.setVisible()
        } else {
            holder.star.setGone()
        }
        holder.cinemaText.text = showing.cinemaName

        GlideApp.with(holder.itemView.context)
            .load("http://www.gencat.cat/llengua/cinema/${showing.moviePosterUrl}")
            .error(R.drawable.poster_placeholder)
            .centerCrop()
            .into(holder.posterImage)
        holder.date.text = dateFormatter.shortDateWithToday(showing.date)
        holder.distance.setVisibleText(if (showing.cinemaDistance == null) null else "≈ ${showing.cinemaDistance} km")

        holder.itemView.setOnClickListener {
            context.startActivity(
                MovieActivity.createIntent(context, showing.movieId, showingId = showing.id)
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.showing_item_horiz, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return filteredShowings.size
    }

    override fun getFilter() = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            var list = listOf<Showing>()
            val maxDistance = constraint.toString().toInt()
            showings.forEach { showing ->
                showing.cinemaDistance?.let { distance ->
                    if (distance < maxDistance)
                        list += showing
                }
            }
            list = list.sortedWith(compareBy<Showing> { it.date }.thenBy { it.cinemaDistance })

            if (list.size > 16)
                list = list.subList(0, 16)
            return FilterResults().apply { values = list }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            results?.values?.let {
                filteredShowings = it as List<Showing>
                notifyDataSetChanged()
                filterListener?.onFilterComplete(it.size)
            }
        }

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var posterImage: ImageView = view.findViewById(R.id.moviePoster)
        var titleText: TextView = view.findViewById(R.id.movieTitle)
        var cinemaText: TextView = view.findViewById(R.id.cinemaName)
        var date: TextView = view.findViewById(R.id.showingDate)
        var distance: TextView = view.findViewById(R.id.showingDistance)
        var star: ImageView = view.findViewById(R.id.star)
    }
}
