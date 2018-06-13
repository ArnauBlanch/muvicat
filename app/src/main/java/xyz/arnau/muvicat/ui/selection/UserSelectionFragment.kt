package xyz.arnau.muvicat.ui.selection

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.selection_fragment.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.ui.cinema.TabViewPagerAdapter
import xyz.arnau.muvicat.ui.utils.ScrollableToTop

class UserSelectionFragment: Fragment(), ScrollableToTop {
    private lateinit var tabAdapter: TabViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.selection_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbar()
        tabAdapter = TabViewPagerAdapter(
            listOf(VotedMoviesListFragment()),
            listOf(R.string.voted),
            activity!!.supportFragmentManager,
            activity!!
        )
        viewPager.adapter = tabAdapter
    }

    private fun setupToolbar() {
        selectionToolbarCollapsing
            .setExpandedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.nunito_sans_black))

        selectionToolbarCollapsing
            .setCollapsedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.nunito_sans_black))

        selectionToolbar.setOnClickListener { scrollToTop() }
    }

    override fun scrollToTop() {
        (tabAdapter.getItem(0) as ScrollableToTop).scrollToTop()
        selectionToolbarLayout.setExpanded(true)
    }

    companion object {
        const val FRAG_ID = 4
    }
}
