package xyz.arnau.muvicat.ui

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent


class NonSwipeableViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    private var mSwipeEnabled: Boolean = false

    init {
        this.mSwipeEnabled = false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (mSwipeEnabled) {
            super.onTouchEvent(event)
        } else false

    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (mSwipeEnabled) {
            super.onInterceptTouchEvent(event)
        } else false

    }

    fun setCurrentItemOrScrollToTop(item: Int, smoothScroll: Boolean) {
        if (super.getCurrentItem() == item) {
            ((super.getAdapter() as ViewPagerAdapter).getItem(item) as ScrollableToTop)
                .scrollToTop()
        }
        super.setCurrentItem(item, smoothScroll)
    }
}