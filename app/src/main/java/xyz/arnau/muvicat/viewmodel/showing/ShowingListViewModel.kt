package xyz.arnau.muvicat.viewmodel.showing

import android.arch.lifecycle.ViewModel
import xyz.arnau.muvicat.repository.ShowingRepository
import javax.inject.Inject

class ShowingListViewModel @Inject constructor(showingRepository: ShowingRepository) : ViewModel() {
    val showings = showingRepository.getShowings()
}