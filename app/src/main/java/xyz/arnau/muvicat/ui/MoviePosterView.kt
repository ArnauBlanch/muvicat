package xyz.arnau.muvicat.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

/**
 * ImageView that keeps aspect ratio when scaled
 */
class MoviePosterView : ImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val newHeight = MeasureSpec.getSize(widthMeasureSpec) * (7 / 10)
        val newHeightSpec = MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, newHeightSpec)
    }

}