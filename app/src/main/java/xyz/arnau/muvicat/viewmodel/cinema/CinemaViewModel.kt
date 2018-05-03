package xyz.arnau.muvicat.viewmodel.cinema

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import xyz.arnau.muvicat.data.CinemaRepository
import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.data.model.Resource
import javax.inject.Inject

class CinemaViewModel @Inject constructor(cinemaRepository: CinemaRepository) : ViewModel() {
    private val cinemaId = MutableLiveData<Long>()

    val cinema: LiveData<Resource<Cinema>> = Transformations.switchMap(cinemaId) { id ->
        cinemaRepository.getCinema(id)
    }

    fun setId(id: Long) {
        cinemaId.value = id
    }
}