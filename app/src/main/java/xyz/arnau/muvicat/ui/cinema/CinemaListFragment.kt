package xyz.arnau.muvicat.ui.cinema

import android.app.Activity
import android.arch.lifecycle.Observer
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.cinema_list.*
import kotlinx.android.synthetic.main.cinema_list_toolbar.*
import kotlinx.android.synthetic.main.error_layout.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.repository.model.Cinema
import xyz.arnau.muvicat.repository.model.Resource
import xyz.arnau.muvicat.repository.model.Status
import xyz.arnau.muvicat.di.Injectable
import xyz.arnau.muvicat.ui.*
import xyz.arnau.muvicat.ui.utils.ScrollableToTop
import xyz.arnau.muvicat.ui.utils.SimpleDividerItemDecoration
import xyz.arnau.muvicat.utils.LocationUtils
import xyz.arnau.muvicat.viewmodel.cinema.CinemaListViewModel
import javax.inject.Inject


class CinemaListFragment : ListFragment(), ScrollableToTop, Injectable {
    @Inject
    lateinit var cinemasAdapter: CinemaListAdapter

    @Inject
    lateinit var cinemaListViewModel: CinemaListViewModel

    private lateinit var skeleton: RecyclerViewSkeletonScreen
    private var hasLocation = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbar()
        setupSkeletonScreen()
        getRecyclerView().addItemDecoration(
            SimpleDividerItemDecoration(
                context!!
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cinemas_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        cinemaListViewModel.cinemas.observe(this,
            Observer<Resource<List<Cinema>>> {
                if (it != null) handleDateState(it.status, it.data)
            })
    }

    override fun onResume() {
        super.onResume()
        if (!hasLocation && getLastLocation() != null) {
            notifyLastLocation(getLastLocation()!!)
        }
        if ((activity as MainActivity).isSelectedFragment(FRAG_ID)) context?.let {
            FirebaseAnalytics.getInstance(it)
                .setCurrentScreen(activity as Activity, "Cinema list", "Cinema list")
            FirebaseAnalytics.getInstance(it)
        }
    }

    private fun handleDateState(status: Status, data: List<Cinema>?) {
        if (status == Status.SUCCESS) data?.let {
            updateCinemaList(it, getLastLocation())
            skeleton.hide()
            restoreRecyclerViewState()
            if (data.isEmpty()) {
                cinemasRecyclerView.visibility = View.GONE
                errorMessage.visibility = View.VISIBLE
            }
        } else if (status == Status.ERROR) {
            skeleton.hide()
            if (data != null && !data.isEmpty()) {
                updateCinemaList(data, getLastLocation())
                skeleton.hide()
                view?.let {
                    Snackbar.make(it, getString(R.string.couldnt_update_data), 6000)
                        .show()
                }
            } else {
                cinemasRecyclerView.visibility = View.GONE
                errorMessage.visibility = View.VISIBLE
            }
        }
    }

    private fun updateCinemaList(data: List<Cinema>, lastLocation: Location?) {
        if (lastLocation != null) {
            setLocationToCinemas(data, lastLocation)
            hasLocation = true
        }
        cinemasAdapter.cinemas =
                data.sortedWith(compareBy<Cinema, Int?>(nullsLast(), { it.distance }))
        cinemasAdapter.notifyDataSetChanged()
    }

    private fun setupToolbar() {
        cinemasToolbarCollapsing
            .setExpandedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.nunito_sans_black))
        cinemasToolbarCollapsing
            .setCollapsedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.nunito_sans_black))
        cinemasToolbar.setOnClickListener { scrollToTop() }
    }

    private fun setupSkeletonScreen() {
        skeleton = Skeleton.bind(cinemasRecyclerView)
            .adapter(cinemasAdapter)
            .count(8)
            .color(R.color.skeleton_shimmer)
            .load(R.layout.cinema_item_skeleton)
            .show()
    }

    private fun getLastLocation() = (activity as LocationAwareActivity).lastLocation

    fun notifyLastLocation(lastLocation: Location) {
        if (::cinemasAdapter.isInitialized) {
            updateCinemaList(cinemasAdapter.cinemas, lastLocation)
        } else {
            hasLocation = false
        }
    }

    private fun setLocationToCinemas(cinemaList: List<Cinema>, location: Location) {
        cinemaList.forEach {
            if (it.latitude != null && it.longitude != null) {
                it.distance = LocationUtils.getDistance(location, it.latitude!!, it.longitude!!)
            }
        }
    }

    override fun getRecyclerView(): RecyclerView = cinemasRecyclerView

    override fun getRecyclerViewAdapter(): CinemaListAdapter = cinemasAdapter

    override fun getRecyclerViewLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(context)

    override fun scrollToTop() {
        cinemasRecyclerView?.scrollToPosition(0)
        cinemasToolbarLayout?.setExpanded(true)
    }

    companion object {
        const val FRAG_ID = 2
    }
}