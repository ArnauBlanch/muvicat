package xyz.arnau.muvicat.ui.showing

import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.showing_list.*
import kotlinx.android.synthetic.main.showing_list_toolbar.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.repository.model.Showing
import xyz.arnau.muvicat.ui.LocationAwareActivity
import xyz.arnau.muvicat.ui.MainActivity
import xyz.arnau.muvicat.ui.ScrollableToTop
import xyz.arnau.muvicat.utils.LocationUtils
import xyz.arnau.muvicat.viewmodel.showing.ShowingListViewModel
import javax.inject.Inject

class ShowingListFragment : BasicShowingListFragment<Showing>(), ScrollableToTop {
    @Inject
    lateinit var showingsAdapter: ShowingListAdapter

    @Inject
    lateinit var showingListViewModel: ShowingListViewModel

    private var hasLocation = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbar()
    }

    override fun getShowingsLiveData() = showingListViewModel.showings

    override fun onResume() {
        super.onResume()
        if (!hasLocation && getLastLocation() != null) {
            notifyLastLocation(getLastLocation()!!)
        }
        if ((activity as MainActivity).isSelectedFragment(FRAG_ID)) context?.let {
            FirebaseAnalytics.getInstance(it)
                .setCurrentScreen(activity as Activity, "Showing list", "Showing list")
            FirebaseAnalytics.getInstance(it)
        }
    }

    override fun handleShowingsUpdate(data: List<Showing>) {
        handleShowingsUpdate(data, getLastLocation())
    }

    private fun handleShowingsUpdate(data: List<Showing>, lastLocation: Location?) {
        if (lastLocation != null) {
            setLocationToShowings(data, lastLocation)
            hasLocation = true
        }
        showingsAdapter.showings = data.sortedWith(
            compareBy<Showing> { it.date }.thenBy(
                nullsLast(),
                { it.cinemaDistance })
        )
        showingsAdapter.notifyDataSetChanged()
    }

    private fun setupToolbar() {
        showingsToolbarCollapsing
            .setExpandedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.nunito_sans_black))

        showingsToolbarCollapsing
            .setCollapsedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.nunito_sans_black))

        showingsToolbar.setOnClickListener {
            scrollToTop()
        }
    }

    private fun getLastLocation() = (activity as LocationAwareActivity).lastLocation

    private fun notifyLastLocation(lastLocation: Location) {
        if (::showingsAdapter.isInitialized) {
            handleShowingsUpdate(showingsAdapter.showings, lastLocation)
        } else {
            hasLocation = false
        }
    }

    private fun setLocationToShowings(showingList: List<Showing>, location: Location) {
        showingList.forEach {
            if (it.cinemaLatitude != null && it.cinemaLongitude != null) {
                it.cinemaDistance = LocationUtils.getDistance(
                    location,
                    it.cinemaLatitude!!,
                    it.cinemaLongitude!!
                )
            }
        }
    }

    override fun getRecyclerViewAdapter() = showingsAdapter

    override fun scrollToTop() {
        showingsRecyclerView?.scrollToPosition(0)
        showingsToolbarLayout?.setExpanded(true)
    }

    companion object {
        const val FRAG_ID = 1
    }
}