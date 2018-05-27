package xyz.arnau.muvicat.ui.movie

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.movie_list.*
import kotlinx.android.synthetic.main.movie_list_toolbar.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.ui.BackPressable
import xyz.arnau.muvicat.ui.MainActivity
import xyz.arnau.muvicat.ui.ScrollableToTop
import xyz.arnau.muvicat.viewmodel.movie.MovieListViewModel
import javax.inject.Inject


class MovieListFragment : BasicMovieListFragment<Movie>(),
    ScrollableToTop, SearchView.OnQueryTextListener, BackPressable {
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
        hideSearch()
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
        moviesToolbarLayout.addOnOffsetChangedListener { _, verticalOffset ->
            if (verticalOffset == 0)
                moviesToolbar.menu.setGroupVisible(0, false)
            else
                moviesToolbar.menu.setGroupVisible(0, true)
        }
        moviesToolbar.inflateMenu(R.menu.movie_list_menu)
        moviesToolbar.setOnMenuItemClickListener {
            if (it?.itemId == R.id.action_search) {
                moviesAdapter.filter
                true
            } else
            false
        }
        val searchView = moviesToolbar.menu.findItem(R.id.action_search).actionView as SearchView
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.setOnQueryTextListener(this)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        moviesAdapter.filter.filter(newText)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        val searchView = moviesToolbar.menu.findItem(R.id.action_search).actionView as SearchView
        moviesAdapter.filter.filter(query)
        searchView.clearFocus()
        return true
    }

    override fun onBackPressed() {
        hideSearch()
    }

    private fun hideSearch() {
        val searchView = moviesToolbar.menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setQuery("", false)
        searchView.isIconified = true
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
