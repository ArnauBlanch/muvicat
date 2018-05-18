package xyz.arnau.muvicat.viewmodel.cinema

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import xyz.arnau.muvicat.repository.CinemaRepository
import xyz.arnau.muvicat.repository.MovieRepository
import xyz.arnau.muvicat.repository.ShowingRepository
import xyz.arnau.muvicat.repository.model.Cinema
import xyz.arnau.muvicat.repository.model.CinemaShowing
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.repository.model.Resource
import javax.inject.Inject

class CinemaViewModel @Inject constructor(
    cinemaRepository: CinemaRepository,
    showingRepository: ShowingRepository,
    movieRepository: MovieRepository
) : ViewModel() {
    private val cinemaId = MutableLiveData<Long>()

    val cinema: LiveData<Resource<Cinema>> = Transformations.switchMap(cinemaId) { id ->
        cinemaRepository.getCinema(id)
    }

    val showings: LiveData<Resource<List<CinemaShowing>>> =
        Transformations.switchMap(cinemaId) { cinemaId ->
            showingRepository.getShowingsByCinema(cinemaId)
        }

    val movies: LiveData<Resource<List<Movie>>> =
        Transformations.switchMap(cinemaId) { cinemaId ->
            movieRepository.getMoviesByCinema(cinemaId)
        }

    fun setId(id: Long) {
        cinemaId.value = id
    }
}