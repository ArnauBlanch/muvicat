package xyz.arnau.muvicat.ui.showing

import android.app.Activity
import android.arch.lifecycle.Observer
import android.location.Location
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.Snackbar
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.movie_list_toolbar.*
import kotlinx.android.synthetic.main.showing_list.*
import kotlinx.android.synthetic.main.showing_list_toolbar.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.model.Showing
import xyz.arnau.muvicat.data.model.Status
import xyz.arnau.muvicat.di.Injectable
import xyz.arnau.muvicat.ui.LocationAwareActivity
import xyz.arnau.muvicat.ui.MainActivity
import xyz.arnau.muvicat.ui.ScrollableFragment
import xyz.arnau.muvicat.ui.SimpleDividerItemDecoration
import xyz.arnau.muvicat.ui.movie.MovieListFragment
import xyz.arnau.muvicat.utils.LocationUtils
import xyz.arnau.muvicat.viewmodel.showing.ShowingListViewModel
import javax.inject.Inject

class ShowingListFragment : ScrollableFragment(), Injectable {
    @Inject
    lateinit var showingsAdapter: ShowingListAdapter

    @Inject
    lateinit var showingListViewModel: ShowingListViewModel

    private lateinit var skeleton: RecyclerViewSkeletonScreen
    private var mSavedRecyclerViewState: Parcelable? = null

    private var hasLocation = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupRecyclerView()
        setupToolbar()
        setupSkeletonScreen()

        val cinemaId = arguments?.getLong("cinemaId")

        if (cinemaId == null) {
            setupToolbar()
        } else {
            showingListViewModel.setCinemaId(cinemaId)
            showingsAdapter.showCinemaInfo = false
            showingsToolbarLayout.visibility = View.GONE
        }
        setupRecyclerView()
        setupSkeletonScreen()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.showing_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        showingListViewModel.showings.observe(this,
            Observer<Resource<List<Showing>>> {
                if (it != null) handleDateState(it.status, it.data)
            })
    }

    override fun onResume() {
        super.onResume()
        if (!hasLocation && getLastLocation() != null) {
            notifyLastLocation(getLastLocation()!!)
        }
        if (activity is MainActivity && (activity as MainActivity).isSelectedFragment(FRAG_ID)) context?.let {
            FirebaseAnalytics.getInstance(it)
                .setCurrentScreen(activity as Activity, "Showing list", "Showing list")
            FirebaseAnalytics.getInstance(it)
        }
        if (mSavedRecyclerViewState != null)
            showingsRecyclerView.layoutManager.onRestoreInstanceState(mSavedRecyclerViewState)
    }

    override fun onPause() {
        super.onPause()
        mSavedRecyclerViewState = showingsRecyclerView.layoutManager.onSaveInstanceState()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (mSavedRecyclerViewState != null)
            showingsRecyclerView.layoutManager.onRestoreInstanceState(mSavedRecyclerViewState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mSavedRecyclerViewState = showingsRecyclerView.layoutManager.onSaveInstanceState()
    }


    private fun handleDateState(status: Status, data: List<Showing>?) {
        if (status == Status.SUCCESS) data?.let {
            updateShowingList(it, getLastLocation())
            skeleton.hide()
            showingsRecyclerView.layoutManager.onRestoreInstanceState(mSavedRecyclerViewState)
            if (data.isEmpty()) {
                showingsRecyclerView.visibility = View.GONE
                errorMessage.visibility = View.VISIBLE
            }
        } else if (status == Status.ERROR) {
            skeleton.hide()
            if (data != null && !data.isEmpty()) {
                updateShowingList(data, getLastLocation())
                skeleton.hide()
                view?.let {
                    Snackbar.make(it, getString(R.string.couldnt_update_data), 6000)
                        .show()
                }
            } else {
                showingsRecyclerView.visibility = View.GONE
                errorMessage.visibility = View.VISIBLE
            }
        }
    }

    private fun updateShowingList(data: List<Showing>, lastLocation: Location?) {
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

    private fun setupSkeletonScreen() {
        skeleton = Skeleton.bind(showingsRecyclerView)
            .adapter(showingsAdapter)
            .count(8)
            .color(R.color.skeleton_shimmer)
            .load(R.layout.showing_item_skeleton)
            .show()
    }

    private fun setupRecyclerView() {
        showingsRecyclerView.layoutManager = LinearLayoutManager(context)
        showingsRecyclerView.layoutManager.onSaveInstanceState()
        showingsRecyclerView.adapter = showingsAdapter
        showingsRecyclerView.isEnabled = false
        showingsRecyclerView.addItemDecoration(SimpleDividerItemDecoration(context!!))
    }

    private fun getLastLocation() = (activity as LocationAwareActivity).lastLocation

    private fun notifyLastLocation(lastLocation: Location) {
        if (::showingsAdapter.isInitialized) {
            updateShowingList(showingsAdapter.showings, lastLocation)
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

    override fun scrollToTop() {
        showingsRecyclerView?.scrollToPosition(0)
        showingsToolbarLayout?.setExpanded(true)
    }

    companion object {
        const val FRAG_ID = 1

        fun prepareShowingListByCinema(cinemaId: Long): ShowingListFragment {
            return ShowingListFragment().apply {
                val bundle = Bundle()
                bundle.putLong("cinemaId", cinemaId)
                this.arguments = bundle
            }
        }
    }
}