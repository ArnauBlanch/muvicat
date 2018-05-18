package xyz.arnau.muvicat.viewmodel.movie

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import xyz.arnau.muvicat.repository.MovieRepository
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.repository.model.Resource
import javax.inject.Inject

class MovieListViewModel @Inject constructor(movieRepository: MovieRepository) : ViewModel() {
    val movies: LiveData<Resource<List<Movie>>> = movieRepository.getMovies()
}