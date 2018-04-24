package xyz.arnau.muvicat.ui.movielist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.movie_grid.*
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        movieListViewModel = ViewModelProviders.of(this, viewModelFactory).get(MovieListViewModel::class.java)
        setupRecyclerView()
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
            Status.SUCCESS -> data?.let { updateMovieList(it) }
            Status.LOADING -> Timber.i("Loading movies...")
            else -> Timber.e("ERROR: $message")
        }
    }

    private fun updateMovieList(data: List<Movie>) {
        moviesAdapter.movies = data
        moviesAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        moviesRecyclerView.layoutManager = GridLayoutManager(context, 2)
        moviesRecyclerView.adapter = moviesAdapter
        moviesRecyclerView.addOnItemTouchListener(object: RecyclerView.SimpleOnItemTouchListener() {
            override fun onTouchEvent(rv: RecyclerView?, e: MotionEvent?) {

            }

        })
    }
}