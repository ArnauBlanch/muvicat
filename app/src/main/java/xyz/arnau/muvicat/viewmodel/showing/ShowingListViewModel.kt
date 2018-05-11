package xyz.arnau.muvicat.viewmodel.showing

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import xyz.arnau.muvicat.data.ShowingRepository
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.model.Showing
import javax.inject.Inject

class ShowingListViewModel @Inject constructor(showingRepository: ShowingRepository) : ViewModel() {
    val showings: LiveData<Resource<List<Showing>>> = showingRepository.getShowings()
}