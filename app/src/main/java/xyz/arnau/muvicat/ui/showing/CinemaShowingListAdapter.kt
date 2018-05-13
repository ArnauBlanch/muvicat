package xyz.arnau.muvicat.ui.showing

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.data.model.CinemaShowing
import xyz.arnau.muvicat.ui.movie.MovieActivity
import xyz.arnau.muvicat.utils.DateFormatter
import xyz.arnau.muvicat.utils.GlideApp
import javax.inject.Inject


class CinemaShowingListAdapter @Inject constructor() :
    RecyclerView.Adapter<CinemaShowingListAdapter.ViewHolder>() {
    @Inject
    lateinit var dateFormatter: DateFormatter

    @Inject
    lateinit var context: Context

    var showings: List<CinemaShowing> = arrayListOf()

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val showing = showings[position]
        holder.movieTitle.text = showing.movieTitle
        GlideApp.with(holder.itemView.context)
            .load("http://www.gencat.cat/llengua/cinema/${showing.moviePosterUrl}")
            .error(R.drawable.poster_placeholder)
            .centerCrop()
            .into(holder.moviePoster)
        holder.version.text = longerVersion(showing.version)
        holder.date.text = dateFormatter.shortDate(showing.date)

        val posterParams = holder.moviePoster.layoutParams
        posterParams.height = convertDpToPixel(65F)
        holder.moviePoster.layoutParams = posterParams

        holder.distance.text = " "
        holder.dateDistanceMargin.visibility = View.GONE
        holder.distance.visibility = View.GONE

        holder.itemView.setOnClickListener {
            context.startActivity(
                MovieActivity.createIntent(
                    context,
                    showing.movieId
                )
            )
        }
    }

    private fun longerVersion(version: String?): CharSequence? {
        return when (version) {
            "VD" -> "Versió doblada al català"
            "VO" -> "Versió original en català"
            "VOSC" -> "VO subtitulada en català"
            "VOSE" -> "VO en català subt. al castellà"
            else -> null
        }
    }

    override fun getItemCount() = showings.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.showing_item, parent, false)
        return ViewHolder(itemView)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var movieTitle: TextView = view.findViewById(R.id.movieTitle)
        var moviePoster: ImageView = view.findViewById(R.id.moviePoster)
        var version: TextView = view.findViewById(R.id.showingVersion)
        var date: TextView = view.findViewById(R.id.date)
        var distance: TextView = view.findViewById(R.id.cinemaDistance)
        var dateDistanceMargin: View = view.findViewById(R.id.dateDistanceMargin)
    }

    private fun convertDpToPixel(dp: Float): Int {
        val metrics = context.resources.displayMetrics
        val px = dp * (metrics.densityDpi / 160f)
        return Math.round(px)
    }
}