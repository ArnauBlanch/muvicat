package xyz.arnau.muvicat.ui.selection

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.empty_message_layout.*
import kotlinx.android.synthetic.main.movie_list.*
import kotlinx.android.synthetic.main.movie_list_toolbar.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.ui.MainActivity
import xyz.arnau.muvicat.ui.movie.BasicMovieListFragment
import xyz.arnau.muvicat.ui.utils.ScrollableToTop
import xyz.arnau.muvicat.utils.setGone
import xyz.arnau.muvicat.utils.setVisible
import xyz.arnau.muvicat.viewmodel.movie.MovieListViewModel
import javax.inject.Inject


class VotedMoviesListFragment : BasicMovieListFragment<Movie>(), ScrollableToTop {
    @Inject
    lateinit var moviesAdapter: VotedMoviesAdapter

    @Inject
    lateinit var movieListViewModel: MovieListViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        moviesToolbarLayout.setGone()
    }

    override fun onResume() {
        super.onResume()
        if ((activity as MainActivity).isSelectedFragment(FRAG_ID)) context?.let {
            FirebaseAnalytics.getInstance(it)
                .setCurrentScreen(activity as Activity, "User selection", "User selection")
        }
    }

    override fun handleMoviesUpdate(movies: List<Movie>) {
        moviesAdapter.movies = movies
        moviesAdapter.notifyDataSetChanged()
    }

    override fun displayEmptyOrErrorMessage() {
        message.text = getString(R.string.no_voted_movies)
        messageLayout.setVisible()
    }


    override fun scrollToTop() {
        moviesRecyclerView?.scrollToPosition(0)
        moviesToolbarLayout?.setExpanded(true)
    }

    override fun getMoviesLiveData() = movieListViewModel.votedMovies

    override fun getRecyclerView(): RecyclerView = moviesRecyclerView

    override fun getRecyclerViewAdapter() = moviesAdapter

    override fun prepareSkeletonScreen(): RecyclerViewSkeletonScreen =
        Skeleton.bind(moviesRecyclerView)
            .adapter(getRecyclerViewAdapter())
            .count(6)
            .color(R.color.skeleton_shimmer)
            .load(R.layout.movie_item_vert_skeleton)
            .show()

    companion object {
        const val FRAG_ID = 3
    }
}
