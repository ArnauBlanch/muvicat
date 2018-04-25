package xyz.arnau.muvicat.ui.movielist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import kotlinx.android.synthetic.main.movie_fragment.*
import kotlinx.android.synthetic.main.movie_grid.*
import kotlinx.android.synthetic.main.movie_toolbar.*
import timber.log.Timber
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.model.Status
import xyz.arnau.muvicat.di.Injectable
import xyz.arnau.muvicat.viewmodel.movie.MovieListViewModel
import javax.inject.Inject

class MovieListFragment : Fragment(), Injectable {

    @Inject
    lateinit var moviesAdapter: MovieListAdapter

    @Inject
    lateinit var movieListViewModel: MovieListViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var skeleton: RecyclerViewSkeletonScreen

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        movieListViewModel = ViewModelProviders.of(this, viewModelFactory).get(MovieListViewModel::class.java)
        setupRecyclerView()

        moviesToolbarCollapsing.setExpandedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.nunito_sans_black))
        moviesToolbarCollapsing.setCollapsedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.nunito_sans_black))


        skeleton = Skeleton.bind(moviesRecyclerView)
                .adapter(moviesAdapter)
                .count(8)
                .color(R.color.skeleton_shimmer)
                .load(R.layout.movie_card_skeleton)
                .show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.i("HELLO")
        return inflater.inflate(R.layout.movie_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        movieListViewModel.movies.observe(this,
                Observer<Resource<List<Movie>>> {
                    if (it != null) handleDataState(it.status, it.data, it.message)
                })
    }

    private fun handleDataState(status: Status, data: List<Movie>?, message: String?) {
        when (status) {
            Status.SUCCESS -> data?.let {
                updateMovieList(it)
                skeleton.hide()
            }
        /*Status.LOADING -> if (data != null && !data.isEmpty()) {
                updateMovieList(data)
                skeleton.hide()
            }*/
            Status.ERROR -> {
                skeleton.hide()
                if (data != null) {
                    updateMovieList(data)
                    skeleton.hide()
                    view?.let {
                        Snackbar.make(it, getString(R.string.couldnt_update_data), 10000)
                                .show()
                    }
                } else {
                    moviesRecyclerView.visibility = View.GONE
                    errorMessage.visibility = View.VISIBLE
                }
            }

        }
    }

    private fun updateMovieList(data: List<Movie>) {
        moviesAdapter.movies = data
        moviesAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        moviesRecyclerView.layoutManager = GridLayoutManager(context, 2)
        moviesRecyclerView.adapter = moviesAdapter
        moviesRecyclerView.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onTouchEvent(rv: RecyclerView?, e: MotionEvent?) {

            }

        })
    }
}