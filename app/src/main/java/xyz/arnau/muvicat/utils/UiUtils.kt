package xyz.arnau.muvicat.utils

import android.view.View
import android.widget.TextView

fun TextView.setVisibleText(text: String?) {
    text?.let {
        this.text = text
        this.setVisible()
    }
}

fun TextView.setTextAndVisibleLayout(text: String?, layout: View) {
    text?.let {
        this.text = text
        layout.setVisible()
    }
}

fun View.setVisible() {
    this.visibility = View.VISIBLE
}

fun View.setGone() {
    this.visibility = View.GONE
}

fun longerVersion(version: String?): CharSequence? {
    return when (version) {
        "VD" -> "Versió doblada al català"
        "VO" -> "Versió original en català"
        "VOSC" -> "VO subtitulada en català"
        "VOSE" -> "VO en català subt. al castellà"
        else -> null
    }
}