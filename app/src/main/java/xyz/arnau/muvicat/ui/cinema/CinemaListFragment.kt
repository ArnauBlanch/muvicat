package xyz.arnau.muvicat.ui.cinema

import android.app.Activity
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
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
import xyz.arnau.muvicat.data.model.CinemaInfo
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.model.Status
import xyz.arnau.muvicat.di.Injectable
import xyz.arnau.muvicat.ui.MainActivity
import xyz.arnau.muvicat.ui.SimpleDividerItemDecoration
import xyz.arnau.muvicat.viewmodel.cinema.CinemaListViewModel
import javax.inject.Inject


class CinemaListFragment : Fragment(), Injectable {
    @Inject
    lateinit var cinemasAdapter: CinemaListAdapter

    @Inject
    lateinit var cinemaListViewModel: CinemaListViewModel

    private lateinit var skeleton: RecyclerViewSkeletonScreen
    private var mSavedRecyclerViewState: Parcelable? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupRecyclerView()
        setupToolbar()
        setupSkeletonScreen()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cinema_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        cinemaListViewModel.cinemas.observe(this,
            Observer<Resource<List<CinemaInfo>>> {
                if (it != null) handleDateState(it.status, it.data)
            })
    }

    override fun onResume() {
        super.onResume()
        if ((activity as MainActivity).isSelectedFragment(FRAG_ID)) context?.let {
            FirebaseAnalytics.getInstance(it)
                .setCurrentScreen(activity as Activity, "Cinema list", "Cinema list")
            FirebaseAnalytics.getInstance(it)
        }
        if (mSavedRecyclerViewState != null)
            cinemasRecyclerView.layoutManager.onRestoreInstanceState(mSavedRecyclerViewState)
    }

    override fun onPause() {
        super.onPause()
        mSavedRecyclerViewState = cinemasRecyclerView.layoutManager.onSaveInstanceState()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (mSavedRecyclerViewState != null)
            cinemasRecyclerView.layoutManager.onRestoreInstanceState(mSavedRecyclerViewState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mSavedRecyclerViewState = cinemasRecyclerView.layoutManager.onSaveInstanceState()
    }


    private fun handleDateState(status: Status, data: List<CinemaInfo>?) {
        if (status == Status.SUCCESS) data?.let {
            updateCinemaList(it)
            skeleton.hide()
            cinemasRecyclerView.layoutManager.onRestoreInstanceState(mSavedRecyclerViewState)
        } else if (status == Status.ERROR) {
            skeleton.hide()
            if (data != null && !data.isEmpty()) {
                updateCinemaList(data)
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

    private fun updateCinemaList(data: List<CinemaInfo>) {
        cinemasAdapter.cinemas = data
        cinemasAdapter.notifyDataSetChanged()
    }

    private fun setupToolbar() {
        cinemasToolbarCollapsing
            .setExpandedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.nunito_sans_black))

        cinemasToolbarCollapsing
            .setCollapsedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.nunito_sans_black))

        cinemasToolbar.setOnClickListener {
            cinemasRecyclerView.scrollToPosition(0)
            cinemasToolbarLayout.setExpanded(true)
        }
    }

    private fun setupSkeletonScreen() {
        skeleton = Skeleton.bind(cinemasRecyclerView)
            .adapter(cinemasAdapter)
            .count(8)
            .color(R.color.skeleton_shimmer)
            .load(R.layout.cinema_item_skeleton)
            .show()
    }

    private fun setupRecyclerView() {
        cinemasRecyclerView.layoutManager = LinearLayoutManager(context)
        cinemasRecyclerView.layoutManager.onSaveInstanceState()
        cinemasRecyclerView.adapter = cinemasAdapter
        cinemasRecyclerView.isEnabled = false
        cinemasRecyclerView.addItemDecoration(SimpleDividerItemDecoration(context!!))
    }

    companion object {
        const val FRAG_ID = 1
    }
}