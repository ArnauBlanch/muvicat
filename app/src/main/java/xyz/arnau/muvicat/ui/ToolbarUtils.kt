package xyz.arnau.muvicat.ui

import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import xyz.arnau.muvicat.R

object ToolbarUtils {
    fun setupCollapsingToolbar(
        activity: AppCompatActivity,
        toolbarId: Int,
        appBarLayoutId: Int,
        collapsingLayoutId: Int,
        title: String?,
        scrollableToTop: ScrollableToTop
    ) {
        activity.findViewById<Toolbar>(toolbarId)?.let { toolbar ->
            activity.findViewById<AppBarLayout>(appBarLayoutId)?.let { appBarLayout ->
                activity.findViewById<CollapsingToolbarLayout>(collapsingLayoutId)
                    ?.let { collapsingToolbarLayout ->
                        activity.setSupportActionBar(toolbar)
                        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
                        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
                        collapsingToolbarLayout.setCollapsedTitleTypeface(
                            ResourcesCompat.getFont(activity, R.font.nunito_sans_extrabold)
                        )

                        toolbar.setNavigationOnClickListener { activity.onBackPressed() }

                        appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                            if (verticalOffset < -300)
                                activity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_chevron_left_red)
                            else
                                activity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_chevron_left_white)

                            if (appBarLayout.totalScrollRange + verticalOffset == 0) {
                                title?.let { collapsingToolbarLayout.title = it }
                            } else {
                                collapsingToolbarLayout.title = " "
                            }
                        }

                        toolbar.setOnClickListener {
                            appBarLayout.setExpanded(true)
                            scrollableToTop.scrollToTop()
                        }
                    }
            }
        }

    }
}