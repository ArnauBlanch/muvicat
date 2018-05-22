package xyz.arnau.muvicat.ui.movie

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import kotlinx.android.synthetic.main.rating_dialog.*
import xyz.arnau.muvicat.repository.model.Movie
import java.text.DecimalFormat

class RatingDialog(private val activity: MovieActivity, private val movie: Movie) :
    BottomSheetDialog(activity) {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userRatingValue.text = if (movie.vote == null)
            "${2.5.toString1Decimal()} / ${5.0.toString1Decimal()}"
        else
            "${movie.vote!!.div(2).toString1Decimal()} / ${5.0.toString1Decimal()}"
        userRatingBar.rating = if (movie.vote == null) 2.5F else movie.vote!!.div(2).toFloat()
        userRatingBar.setOnRatingBarChangeListener { ratingBar, rating, _ ->
            if (rating < 0.5F) {
                ratingBar.rating = 0.5F
            }
        }
        userRatingBar.setOnRatingChangeListener { _, rating ->
            userRatingValue.text =
                    "${if (rating < 0.5F) 0.5.toString1Decimal() else rating.toDouble().toString1Decimal()} / ${5.0.toString1Decimal()}"
        }
        sendRatingButton.setOnClickListener {
            activity.rateMovie(movie.tmdbId!!, userRatingBar.rating.toDouble() * 2)
            dismiss()
        }
    }

    private fun Double.toString1Decimal(): String = DecimalFormat("0.0").format(this)
}