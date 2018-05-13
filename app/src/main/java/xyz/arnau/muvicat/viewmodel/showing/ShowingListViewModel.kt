package xyz.arnau.muvicat.viewmodel.showing

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import xyz.arnau.muvicat.data.ShowingRepository
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.model.Showing
import javax.inject.Inject

class ShowingListViewModel @Inject constructor(showingRepository: ShowingRepository) : ViewModel() {
    private val cinemaId = MutableLiveData<Long>()

    init {
        cinemaId.value = null
    }

    val showings: LiveData<Resource<List<Showing>>> = Transformations.switchMap(cinemaId) { cinemaId ->
        if (cinemaId == null) {
            showingRepository.getShowings()
        } else {
            showingRepository.getShowingsByCinema(cinemaId)
        }
    }

    fun setCinemaId(id: Long) {
        cinemaId.value = id
    }
}