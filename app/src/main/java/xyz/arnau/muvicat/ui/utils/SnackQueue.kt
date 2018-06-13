package xyz.arnau.muvicat.ui.utils

import android.support.design.widget.Snackbar
import java.util.*


class SnackQueue {
    internal var snackbars: Queue<QueuedSnack> = LinkedList()
    var noLocationShown = false
    var couldntUpdateShown = false

    /**
     * This method adds snackbars onto the snackbar queue and gives them a callback
     *
     * @param queuedSnack
     */
    fun enqueueSnack(queuedSnack: QueuedSnack, code: Int? = null) {
        when {
            code == COULDNT_UPDATE && !couldntUpdateShown -> {
                enqueue(queuedSnack)
                couldntUpdateShown = true
            }
            code == COULDNT_GET_LOCATION && !noLocationShown -> {
                enqueue(queuedSnack)
                noLocationShown = true
            }
            code == null -> enqueue(queuedSnack)
        }
    }

    private fun enqueue(queuedSnack: QueuedSnack) {
        val snackbar = queuedSnack.snackbar
        if (snackbars.isEmpty()) {
            snackbar.show()
        }
        snackbars.add(queuedSnack)
        snackbar.setCallback(object : Snackbar.Callback() {
            override fun onDismissed(snackbar: Snackbar?, event: Int) {
                super.onDismissed(snackbar, event)
                if (event == Snackbar.Callback.DISMISS_EVENT_ACTION || event == Snackbar.Callback
                        .DISMISS_EVENT_TIMEOUT
                ) {
                    snackbars.remove()
                    showNextSnack()
                }
            }
        })
    }

    /**
     * Finds next available snack in the queue and shows it
     */
    private fun showNextSnack() {
        if (snackbars.isNotEmpty()) {
            if (snackbars.peek() != null) {
                snackbars.peek().snackbar.show()
            } else {
                snackbars.remove()
                showNextSnack()
            }
        }
    }

    companion object {
        const val COULDNT_UPDATE = 0
        const val COULDNT_GET_LOCATION = 1
    }
}