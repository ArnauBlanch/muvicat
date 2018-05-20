package xyz.arnau.muvicat.ui.movie

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.movie_list.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.repository.model.Resource
import xyz.arnau.muvicat.repository.model.Status
import xyz.arnau.muvicat.di.Injectable
import xyz.arnau.muvicat.ui.ListFragment
import xyz.arnau.muvicat.utils.setGone
import xyz.arnau.muvicat.utils.setVisible

abstract class BasicMovieListFragment<T> : ListFragment(),
    Injectable {
    private lateinit var skeleton: RecyclerViewSkeletonScreen

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupSkeletonScreen()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.movies_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        getMoviesLiveData().observe(this,
            Observer<Resource<List<T>>> {
                if (it != null) handleDataState(it.status, it.data)
            })
    }

    internal abstract fun getMoviesLiveData(): LiveData<Resource<List<T>>>

    private fun handleDataState(status: Status, data: List<T>?) {
        if (status == Status.SUCCESS) data?.let {
            handleMoviesUpdate(it)
            skeleton.hide()
            restoreRecyclerViewState()
            if (data.isEmpty()) {
                moviesRecyclerView.setGone()
                errorMessage.setVisible()
            }
        }
        else if (status == Status.ERROR) {
            skeleton.hide()
            if (data != null && !data.isEmpty()) {
                handleMoviesUpdate(data)
                skeleton.hide()
                view?.let {
                    Snackbar.make(it, getString(R.string.couldnt_update_data), 6000)
                        .show()
                }
            } else {
                moviesRecyclerView.setGone()
                errorMessage.setVisible()
            }
        }
    }

    abstract fun handleMoviesUpdate(movies: List<T>)

    override fun getRecyclerView(): RecyclerView = moviesRecyclerView

    override fun getRecyclerViewLayoutManager(): RecyclerView.LayoutManager = GridLayoutManager(context, 2)

    private fun setupSkeletonScreen() {
        skeleton = Skeleton.bind(moviesRecyclerView)
            .adapter(getRecyclerViewAdapter())
            .count(6)
            .color(R.color.skeleton_shimmer)
            .load(R.layout.movie_item_skeleton)
            .show()
    }
}
