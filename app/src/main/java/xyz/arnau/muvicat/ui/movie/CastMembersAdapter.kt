package xyz.arnau.muvicat.ui.movie

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.repository.model.CastMember
import xyz.arnau.muvicat.utils.GlideApp
import javax.inject.Inject

class CastMembersAdapter @Inject constructor() : RecyclerView.Adapter<CastMembersAdapter.ViewHolder>() {
    @Inject
    lateinit var context: Context

    var cast: List<CastMember> = listOf()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val castMember = cast[position]
        holder.name.text = castMember.name
        holder.character.text = castMember.character

        GlideApp.with(holder.itemView.context)
            .load("https://image.tmdb.org/t/p/w300/${castMember.profile_path}")
            .placeholder(R.drawable.people_placeholder)
            .centerCrop()
            .into(holder.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cast_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return cast.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView = view.findViewById(R.id.castMemberImage)
        var name: TextView = view.findViewById(R.id.castMemberName)
        var character: TextView = view.findViewById(R.id.castMemberCharacter)
    }
}
