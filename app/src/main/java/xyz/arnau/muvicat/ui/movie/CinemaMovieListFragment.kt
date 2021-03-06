package xyz.arnau.muvicat.ui.movie

import android.os.Bundle
import kotlinx.android.synthetic.main.movie_list_toolbar.*
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.di.Injectable
import xyz.arnau.muvicat.ui.cinema.CinemaActivity
import xyz.arnau.muvicat.utils.setGone
import xyz.arnau.muvicat.viewmodel.cinema.CinemaViewModel
import javax.inject.Inject

class CinemaMovieListFragment : BasicMovieListFragment<Movie>(),
    Injectable {
    @Inject
    lateinit var moviesAdapter: MovieListAdapter

    private lateinit var viewModel: CinemaViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = (activity as CinemaActivity).viewModel
        moviesAdapter.cinemaId = (activity as CinemaActivity).cinemaId
        moviesToolbarLayout.setGone()
    }

    override fun getMoviesLiveData() = viewModel.movies

    override fun getRecyclerViewAdapter() = moviesAdapter

    override fun handleMoviesUpdate(movies: List<Movie>) {
        moviesAdapter.movies = movies
        moviesAdapter.notifyDataSetChanged()
    }
}
