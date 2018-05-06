package xyz.arnau.muvicat.viewmodel.cinema

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import xyz.arnau.muvicat.data.CinemaRepository
import xyz.arnau.muvicat.data.model.CinemaInfo
import xyz.arnau.muvicat.data.model.Resource
import javax.inject.Inject

class CinemaListViewModel @Inject constructor(cinemaRepository: CinemaRepository) : ViewModel() {
    val cinemas: LiveData<Resource<List<CinemaInfo>>> = cinemaRepository.getCinemas()
}