package xyz.arnau.muvicat.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.widget.SeekBar
import kotlinx.android.synthetic.main.distance_dialog.*

class DistanceDialog(private val fragment: HomeFragment) :
    BottomSheetDialog(fragment.context!!) {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        distanceSeekBar.progress = fragment.nearbyDistance
        distanceValue.text = "≈ ${fragment.nearbyDistance} km"
        distanceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                distanceValue.text = "≈ $progress km"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        okButton.setOnClickListener {
            fragment.nearbyDistance = distanceSeekBar.progress
            dismiss()
        }
    }
}