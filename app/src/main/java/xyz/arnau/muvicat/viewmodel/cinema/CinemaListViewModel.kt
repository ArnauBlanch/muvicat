package xyz.arnau.muvicat.viewmodel.cinema

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import xyz.arnau.muvicat.repository.CinemaRepository
import xyz.arnau.muvicat.repository.model.Cinema
import xyz.arnau.muvicat.repository.model.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CinemaListViewModel @Inject constructor(cinemaRepository: CinemaRepository) : ViewModel() {
    val cinemas: LiveData<Resource<List<Cinema>>> = cinemaRepository.getCinemas()
}