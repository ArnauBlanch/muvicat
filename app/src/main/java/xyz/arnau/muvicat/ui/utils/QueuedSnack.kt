package xyz.arnau.muvicat.ui.utils

import android.app.Activity
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import xyz.arnau.muvicat.R


class QueuedSnack(
    activity: Activity,
    message: String,
    duration: Int,
    action: String? = null,
    snackClickListener: View.OnClickListener? = null,
    maxLines: Int? = null
) {

    val snackbar: Snackbar

    private fun buildSnack(
        activity: Activity,
        message: String,
        duration: Int,
        action: String?,
        snackClickListener: View.OnClickListener?,
        maxLines: Int? = null
    ): Snackbar {
        val snack = Snackbar.make(
            activity.findViewById(R.id.placeSnackBar)
                    ?: activity.findViewById(android.R.id.content),
            message,
            duration
        )

        action?.let { a -> snackClickListener?.let { l -> snack.setAction(a, l) } }
        maxLines?.let {
            snack.view.findViewById<TextView>(android.support.design.R.id.snackbar_text).maxLines =
                    it
        }

        val snackbarView = snack.view
        snackbarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.darkGrey))
        return snack
    }

    init {
        snackbar = buildSnack(activity, message, duration, action, snackClickListener, maxLines)
    }
}