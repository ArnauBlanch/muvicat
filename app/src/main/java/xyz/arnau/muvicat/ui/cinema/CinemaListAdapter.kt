package xyz.arnau.muvicat.ui.cinema

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.utils.setVisibleText
import javax.inject.Inject

class CinemaListAdapter @Inject constructor() :
    RecyclerView.Adapter<CinemaListAdapter.ViewHolder>() {
    @Inject
    lateinit var context: Context

    var cinemas: List<Cinema> = arrayListOf()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cinema = cinemas[position]
        holder.name.text = cinema.name
        holder.address.text = if (cinema.region != null)
            "${cinema.town} (${cinema.region})"
        else
            cinema.town

        holder.distance.setVisibleText("≈ ${cinema.distance} km")
        holder.stats.text = formatStats(cinema.numMovies, cinema.numShowings)

        holder.itemView.setOnClickListener {
            context.startActivity(CinemaActivity.createIntent(context, cinema.id))
        }
    }

    override fun getItemCount() = cinemas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cinema_item, parent, false)
        return ViewHolder(itemView)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.cinemaName)
        var address: TextView = view.findViewById(R.id.cinemaAddress)
        var distance: TextView = view.findViewById(R.id.cinemaDistance)
        var stats: TextView = view.findViewById(R.id.cinemaStats)
    }

    fun formatStats(numMovies: Int, numShowings: Int): String {
        val string = ""
        val movies = if (numMovies > 1)
            context.getString(R.string.movies_lower)
        else
            context.getString(R.string.movie_lower)
        val scheduledShowings = if (numShowings > 1)
            context.getString(R.string.scheduled_showings)
        else
            context.getString(R.string.scheduled_showing)

        return "$numMovies $movies, $numShowings $scheduledShowings"
    }
}
