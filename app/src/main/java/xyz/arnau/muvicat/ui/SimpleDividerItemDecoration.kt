package xyz.arnau.muvicat.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import xyz.arnau.muvicat.R


class SimpleDividerItemDecoration(private val context: Context, private val start: Int = 0) : RecyclerView.ItemDecoration() {
    private val mDivider: Drawable = ContextCompat.getDrawable(context, R.drawable.line_divider)!!

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 18F, context.resources.displayMetrics
            )
        )
        val right = parent.width - Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 18F, context.resources.displayMetrics
            )
        )


        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            if (i >= start || parent.getChildLayoutPosition(parent.getChildAt(i)) != 0) {
                val child = parent.getChildAt(i)

                val params = child.layoutParams as RecyclerView.LayoutParams

                val top = child.bottom + params.bottomMargin
                val bottom = top + mDivider.intrinsicHeight

                mDivider.setBounds(left, top, right, bottom)
                mDivider.draw(c)
            }
        }
    }
}