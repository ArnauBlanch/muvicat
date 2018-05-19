package xyz.arnau.muvicat.viewmodel.movie

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import xyz.arnau.muvicat.repository.MovieRepository
import xyz.arnau.muvicat.repository.ShowingRepository
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.repository.model.MovieShowing
import xyz.arnau.muvicat.repository.model.MovieWithCast
import xyz.arnau.muvicat.repository.model.Resource
import javax.inject.Inject

class MovieViewModel @Inject constructor(
    movieRepository: MovieRepository,
    showingRepository: ShowingRepository
) : ViewModel() {
    private val movieId = MutableLiveData<Long>()

    val movie: LiveData<Resource<MovieWithCast>> =
        Transformations.switchMap(movieId) { id ->
            movieRepository.getMovie(id)
        }

    val showings: LiveData<Resource<List<MovieShowing>>> =
        Transformations.switchMap(movieId) { id ->
            showingRepository.getShowingsByMovie(id)
        }

    fun setId(id: Long) {
        movieId.value = id
    }
}