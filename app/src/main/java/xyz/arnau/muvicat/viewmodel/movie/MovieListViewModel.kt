package xyz.arnau.muvicat.viewmodel.movie

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import xyz.arnau.muvicat.data.MovieRepository
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.Resource
import javax.inject.Inject

class MovieListViewModel @Inject constructor(movieRepository: MovieRepository) : ViewModel() {
    private val cinemaId = MutableLiveData<Long>()

    init {
        cinemaId.value = null
    }

    val movies: LiveData<Resource<List<Movie>>> = Transformations.switchMap(cinemaId) { cinemaId ->
        if (cinemaId == null) {
            movieRepository.getMovies()
        } else {
            movieRepository.getMoviesByCinema(cinemaId)
        }
    }

    fun setCinemaId(id: Long) {
        cinemaId.value = id
    }
}