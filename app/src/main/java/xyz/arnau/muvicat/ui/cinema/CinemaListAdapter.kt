package xyz.arnau.muvicat.ui.cinema

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.data.model.Cinema
import javax.inject.Inject

class CinemaListAdapter @Inject constructor() :
    RecyclerView.Adapter<CinemaListAdapter.ViewHolder>() {
    var cinemas: List<Cinema> = arrayListOf()

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cinema = cinemas[position]
        holder.name.text = cinema.name
        if (cinema.region != null)
            holder.address.text = "${cinema.town} (${cinema.region})"
        else
            holder.address.text = cinema.town
    }

    override fun getItemCount() = cinemas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.cinema_item, parent, false)
        return ViewHolder(itemView)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.cinemaName)
        var address: TextView = view.findViewById(R.id.cinemaAddress)
    }
}
