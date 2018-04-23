package xyz.arnau.muvicat.presentation.movie

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.repository.MovieRepository

class MovieListViewModel internal constructor(movieRepository: MovieRepository) : ViewModel() {
    val movies: LiveData<Resource<List<Movie>>> = movieRepository.getMovies()
}