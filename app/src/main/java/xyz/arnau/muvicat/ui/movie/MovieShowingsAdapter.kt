package xyz.arnau.muvicat.ui.movie

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.repository.model.CastMember
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.repository.model.MovieShowing
import xyz.arnau.muvicat.ui.cinema.CinemaActivity
import xyz.arnau.muvicat.utils.*
import javax.inject.Inject

class MovieShowingsAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    @Inject
    lateinit var dateFormatter: DateFormatter

    @Inject lateinit var castMembersAdapter: CastMembersAdapter

    @Inject
    lateinit var context: Context

    private val TYPE_INFO_HEADER = 0
    private val TYPE_SHOWING_ITEM = 1

    var movie: Movie? = null
    var castMembers: List<CastMember> = listOf()
    var showings: List<MovieShowing> = listOf()
    var showingId: Long? = null
    var expanded = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_INFO_HEADER) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.movie_info_header, parent, false)
            return MovieInfoViewHolder(itemView)
        } else if (viewType == TYPE_SHOWING_ITEM) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.movie_showing_item, parent, false)
            return ShowingViewHolder(itemView)
        }

        throw RuntimeException("unknown view type")
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieInfoViewHolder) {
            movie?.let {
                holder.plot.setVisibleText(movie?.plot)
                holder.originalTitle.setTextAndVisibleLayout(movie?.originalTitle, holder.originalTitleLayout)
                holder.direction.setTextAndVisibleLayout(movie?.direction, holder.directionLayout)
                holder.releaseDate.setTextAndVisibleLayout(
                    movie?.releaseDate?.let { dateFormatter.longDate(it) }, holder.releaseDateLayout
                )
                holder.originalLanguage.setTextAndVisibleLayout(movie?.originalLanguage, holder.originalLanguageLayout)
                if (showings.isNotEmpty()) {
                    if (expanded)
                        holder.showingsTitle.setVisibleText(context.getString(R.string.showings))
                    else
                        holder.showingsTitle.setVisibleText(context.getString(R.string.showing))
                }

                if (showingId != null) {
                    holder.moreShowingsButton.setVisible()
                    holder.moreShowingsButton.setOnCheckedChangeListener({ _, isChecked ->
                        expanded = isChecked
                        notifyDataSetChanged()
                    })
                }

                if (castMembers.isNotEmpty()) {
                    holder.castLayout.setVisible()
                    holder.castMembers.setVisible()
                    if (holder.castMembers.adapter == null) {
                        holder.castMembers.adapter = castMembersAdapter
                        holder.castMembers.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    }
                    castMembersAdapter.cast = castMembers
                    castMembersAdapter.notifyDataSetChanged()
                    holder.cast.setGone()
                } else if (movie?.cast != null) {
                    holder.castLayout.setVisible()
                    holder.cast.setVisibleText(movie?.cast)
                    holder.castMembers.setGone()
                }
            }
        } else if (holder is ShowingViewHolder) {
            val showing = if (expanded)
                showings[position - 1]
            else if (!expanded && position == 1)
                if (showings.any { it.id == showingId })
                    showings.first { it.id == showingId }
                else null
            else
                null

            showing?.let {
                holder.version.text = longerVersion(showing.version)
                holder.date.text = dateFormatter.shortDate(showing.date)
                holder.cinemaName.text = showing.cinemaName
                holder.cinemaPlace.text = if (showing.cinemaRegion != null)
                    "${showing.cinemaTown} (${showing.cinemaRegion})"
                else
                    showing.cinemaTown

                holder.distance.setVisibleText(if (showing.cinemaDistance == null) null else "≈ ${showing.cinemaDistance} km")

                if (showing.cinemaDistance == null)
                    holder.dateDistanceMargin.setGone()
                else
                    holder.dateDistanceMargin.setVisible()

                holder.itemView.setOnClickListener {
                    context.startActivity(CinemaActivity.createIntent(context, showing.cinemaId))
                }
            }
        }
    }

    override fun getItemCount(): Int =
        if (expanded)
            showings.size + 1
        else
            2

    override fun getItemViewType(position: Int): Int {
        return if (position == 0)
            TYPE_INFO_HEADER
        else
            TYPE_SHOWING_ITEM
    }

    inner class MovieInfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var plot: TextView = view.findViewById(R.id.moviePlot)
        var originalTitle: TextView = view.findViewById(R.id.movieOriginalTitle)
        var originalTitleLayout: View = view.findViewById(R.id.movieOriginalTitleLayout)
        var direction: TextView = view.findViewById(R.id.movieDirection)
        var directionLayout: View = view.findViewById(R.id.movieDirectionLayout)
        var releaseDate: TextView = view.findViewById(R.id.movieReleaseDate)
        var releaseDateLayout: View = view.findViewById(R.id.movieReleaseDateLayout)
        var originalLanguage: TextView = view.findViewById(R.id.movieOriginalLanguage)
        var originalLanguageLayout: View = view.findViewById(R.id.movieOriginalLanguageLayout)
        var cast: TextView = view.findViewById(R.id.movieCast)
        var castLayout: View = view.findViewById(R.id.movieCastLayout)
        var castMembers: RecyclerView = view.findViewById(R.id.castRecyclerView)
        var showingsTitle: TextView = view.findViewById(R.id.movieShowingsTitle)
        var moreShowingsButton: ToggleButton = view.findViewById(R.id.moreShowingsButton)
    }

    inner class ShowingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var version: TextView = view.findViewById(R.id.showingVersion)
        var date: TextView = view.findViewById(R.id.date)
        var cinemaName: TextView = view.findViewById(R.id.cinemaName)
        var cinemaPlace: TextView = view.findViewById(R.id.cinemaPlace)
        var distance: TextView = view.findViewById(R.id.cinemaDistance)
        var dateDistanceMargin: View = view.findViewById(R.id.dateDistanceMargin)
    }
}
