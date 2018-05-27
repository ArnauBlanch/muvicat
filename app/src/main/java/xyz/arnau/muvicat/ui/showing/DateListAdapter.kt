package xyz.arnau.muvicat.ui.showing

import android.annotation.SuppressLint
import android.content.Context
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.joda.time.LocalDate
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.ui.DateFilterable
import xyz.arnau.muvicat.utils.DateFormatter
import javax.inject.Inject


class DateListAdapter @Inject constructor() :
    RecyclerView.Adapter<DateListAdapter.ViewHolder>() {
    @Inject
    lateinit var dateFormatter: DateFormatter

    @Inject
    lateinit var context: Context

    var dates: List<LocalDate> = listOf()
    var currentDate: LocalDate = LocalDate.now()
    lateinit var dateFilterable: DateFilterable
    lateinit var dialog: BottomSheetDialog

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date = dates[position]
        holder.date.text = dateFormatter.mediumDate(date.toDate())
        if (date == currentDate)
            holder.date.setTextColor(ResourcesCompat.getColor(context.resources, R.color.colorAccent, null))
        else
            holder.date.setTextColor(ResourcesCompat.getColor(context.resources, android.R.color.black, null))
        holder.dateItem.setOnClickListener {
            dateFilterable.onDatePicked(dates[position])
            dialog.dismiss()
        }
    }


    override fun getItemCount() = dates.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.date_item, parent, false)
        return ViewHolder(itemView)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var dateItem: View = view.findViewById(R.id.dateItem)
        var date: TextView = view.findViewById(R.id.date)
    }
}