package xyz.arnau.muvicat.ui.movie

import android.app.Activity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.movie_list.*
import kotlinx.android.synthetic.main.movie_list_toolbar.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.ui.MainActivity
import xyz.arnau.muvicat.ui.ScrollableToTop
import xyz.arnau.muvicat.viewmodel.movie.MovieListViewModel
import javax.inject.Inject

class MovieListFragment : BasicMovieListFragment<Movie, MovieListAdapter.ViewHolder>(),
    ScrollableToTop {
    @Inject
    lateinit var moviesAdapter: MovieListAdapter

    @Inject
    lateinit var movieListViewModel: MovieListViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbar()
    }

    override fun getMoviesLiveData() = movieListViewModel.movies

    override fun onResume() {
        super.onResume()
        if ((activity as MainActivity).isSelectedFragment(FRAG_ID)) context?.let {
            FirebaseAnalytics.getInstance(it)
                .setCurrentScreen(activity as Activity, "Movie list", "Movie list")
        }
    }

    override fun handleMoviesUpdate(movies: List<Movie>) {
        moviesAdapter.movies = movies
        moviesAdapter.notifyDataSetChanged()
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

    override fun scrollToTop() {
        moviesRecyclerView?.scrollToPosition(0)
        moviesToolbarLayout?.setExpanded(true)
    }

    override fun getRecyclerViewAdapter() = moviesAdapter

    companion object {
        const val FRAG_ID = 0
    }
}
