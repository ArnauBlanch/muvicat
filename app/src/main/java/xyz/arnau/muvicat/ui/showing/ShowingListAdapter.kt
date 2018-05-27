package xyz.arnau.muvicat.ui.showing

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
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.repository.model.Showing
import xyz.arnau.muvicat.ui.movie.MovieActivity
import xyz.arnau.muvicat.utils.*
import javax.inject.Inject


class ShowingListAdapter @Inject constructor() :
    RecyclerView.Adapter<ShowingListAdapter.ViewHolder>(), Filterable {
    @Inject
    lateinit var dateFormatter: DateFormatter

    @Inject
    lateinit var context: Context

    var showings: List<Showing> = listOf()
    var filteredShowings: List<Showing> = listOf()

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val showing = filteredShowings[position]
        holder.movieTitle.text = showing.movieTitle
        if (showing.movieVoted)
            holder.movieTitle
                .setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_star_yellow_18dp,
                    0
                )
        else
            holder.movieTitle
                .setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)

        GlideApp.with(holder.itemView.context)
            .load("http://www.gencat.cat/llengua/cinema/${showing.moviePosterUrl}")
            .error(R.drawable.poster_placeholder)
            .centerCrop()
            .into(holder.moviePoster)
        holder.version.text = longerVersion(showing.version)
        //holder.date.text = dateFormatter.shortDateWithToday(showing.date)
        holder.cinemaName.text = showing.cinemaName
        holder.cinemaPlace.text = if (showing.cinemaRegion != null)
            "${showing.cinemaTown} (${showing.cinemaRegion})"
        else
            showing.cinemaTown


        /*if (showing.cinemaDistance != null)
            holder.dateDistanceMargin.setVisible()
        else
            holder.dateDistanceMargin.setGone()*/
        holder.distance.setVisibleText(if (showing.cinemaDistance == null) null else "≈ ${showing.cinemaDistance} km")

        holder.itemView.setOnClickListener {
            context.startActivity(MovieActivity.createIntent(context, showing.movieId, showingId = showing.id))
        }
    }

    override fun getItemCount() = filteredShowings.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.showing_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getFilter() = object: Filter() {
        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            results?.values?.let {
                filteredShowings = it as List<Showing>
                notifyDataSetChanged()
            }
        }

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            var list = listOf<Showing>()
            try {
                val dateLong = constraint.toString().toLong()
                showings.forEach {
                    if (dateLong == it.date.time) {
                        list += it
                    }
                }
            } catch (e: NumberFormatException) {
                list = showings
            }

            val filterResults = FilterResults()
            filterResults.values = list
            return filterResults
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var movieTitle: TextView = view.findViewById(R.id.movieTitle)
        var moviePoster: ImageView = view.findViewById(R.id.moviePoster)
        var version: TextView = view.findViewById(R.id.showingVersion)
        var date: TextView = view.findViewById(R.id.date)
        var cinemaName: TextView = view.findViewById(R.id.cinemaName)
        var cinemaPlace: TextView = view.findViewById(R.id.cinemaPlace)
        var distance: TextView = view.findViewById(R.id.cinemaDistance)
        var dateDistanceMargin: View = view.findViewById(R.id.dateDistanceMargin)
    }
}