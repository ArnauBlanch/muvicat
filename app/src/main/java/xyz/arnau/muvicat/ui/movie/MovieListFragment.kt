package xyz.arnau.muvicat.ui.movie

import android.app.Activity
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.Snackbar
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.movie_grid.*
import kotlinx.android.synthetic.main.movie_list_toolbar.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.model.Status
import xyz.arnau.muvicat.di.Injectable
import xyz.arnau.muvicat.ui.MainActivity
import xyz.arnau.muvicat.ui.ScrollableFragment
import xyz.arnau.muvicat.viewmodel.movie.MovieListViewModel
import javax.inject.Inject

class MovieListFragment : ScrollableFragment(), Injectable {
    @Inject
    lateinit var moviesAdapter: MovieListAdapter

    @Inject
    lateinit var movieListViewModel: MovieListViewModel

    private lateinit var skeleton: RecyclerViewSkeletonScreen
    private var mSavedRecyclerLayoutState: Parcelable? = null

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
        return inflater.inflate(R.layout.movie_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        movieListViewModel.movies.observe(this,
            Observer<Resource<List<Movie>>> {
                if (it != null) handleDataState(it.status, it.data)
            })
    }

    override fun onResume() {
        super.onResume()
        if ((activity as MainActivity).isSelectedFragment(FRAG_ID)) context?.let {
            FirebaseAnalytics.getInstance(it)
                .setCurrentScreen(activity as Activity, "Movie list", "Movie list")
        }

        if (mSavedRecyclerLayoutState != null) {
            moviesRecyclerView.layoutManager.onRestoreInstanceState(mSavedRecyclerLayoutState)
        }
    }

    override fun onPause() {
        super.onPause()
        mSavedRecyclerLayoutState = moviesRecyclerView.layoutManager.onSaveInstanceState()
    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (mSavedRecyclerLayoutState != null) {
            moviesRecyclerView.layoutManager.onRestoreInstanceState(mSavedRecyclerLayoutState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        mSavedRecyclerLayoutState = moviesRecyclerView.layoutManager.onSaveInstanceState()
    }


    private fun setupSkeletonScreen() {
        skeleton = Skeleton.bind(moviesRecyclerView)
            .adapter(moviesAdapter)
            .count(6)
            .color(R.color.skeleton_shimmer)
            .load(R.layout.movie_card_skeleton)
            .show()
    }

    private fun setupToolbar() {
        moviesToolbarCollapsing
            .setExpandedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.nunito_sans_black))

        moviesToolbarCollapsing
            .setCollapsedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.nunito_sans_black))

        moviesToolbar.setOnClickListener {
            scrollToTop()
        }
    }

    private fun setupRecyclerView() {
        moviesRecyclerView.layoutManager = GridLayoutManager(context, 2)
        moviesRecyclerView.layoutManager.onSaveInstanceState()
        moviesRecyclerView.adapter = moviesAdapter
        moviesRecyclerView.isEnabled = false
    }


    private fun handleDataState(status: Status, data: List<Movie>?) {
        if (status == Status.SUCCESS) data?.let {
            updateMovieList(it)
            skeleton.hide()
            moviesRecyclerView.layoutManager.onRestoreInstanceState(mSavedRecyclerLayoutState)
        }
        else if (status == Status.ERROR) {
            skeleton.hide()
            if (data != null && !data.isEmpty()) {
                updateMovieList(data)
                skeleton.hide()
                view?.let {
                    Snackbar.make(it, getString(R.string.couldnt_update_data), 6000)
                        .show()
                }
            } else {
                moviesRecyclerView.visibility = View.GONE
                errorMessage.visibility = View.VISIBLE
            }
        }
    }

    private fun updateMovieList(data: List<Movie>) {
        moviesAdapter.movies = data
        moviesAdapter.notifyDataSetChanged()
    }

    override fun scrollToTop() {
        moviesRecyclerView.scrollToPosition(0)
        moviesToolbarLayout.setExpanded(true)
    }

    companion object {
        const val FRAG_ID = 0
    }
}
