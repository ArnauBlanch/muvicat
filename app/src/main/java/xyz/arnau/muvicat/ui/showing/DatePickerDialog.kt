package xyz.arnau.muvicat.ui.showing

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.date_picker_dialog.*

class DatePickerDialog(context: Context, private val datesAdapter: DateListAdapter) :
    BottomSheetDialog(context) {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        datesAdapter.dialog = this
        datesRecyclerView.layoutManager = LinearLayoutManager(context)
        datesRecyclerView.adapter = datesAdapter
    }
}