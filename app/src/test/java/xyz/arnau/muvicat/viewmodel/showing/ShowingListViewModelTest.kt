package xyz.arnau.muvicat.viewmodel.showing

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import xyz.arnau.muvicat.data.ShowingRepository
import xyz.arnau.muvicat.data.model.CinemaShowing
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.model.Showing
import xyz.arnau.muvicat.data.model.Status
import xyz.arnau.muvicat.data.test.*
import xyz.arnau.muvicat.utils.getValueBlocking

@RunWith(JUnit4::class)
class ShowingListViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val showingRepository = mock(ShowingRepository::class.java)
    private lateinit var showingListViewModel: ShowingListViewModel
    private val showingsLiveData = MutableLiveData<Resource<List<Showing>>>()

    @Before
    fun setUp() {
        `when`(showingRepository.getShowings()).thenReturn(showingsLiveData)
        showingListViewModel = ShowingListViewModel(showingRepository)
    }

    @Test
    fun getShowingsReturnsLiveData() {
        val showings = ShowingMapper.mapFromShowingEntityList(ShowingEntityFactory.makeShowingEntityList(5))
        showingsLiveData.postValue(Resource.success(showings))

        val result = showingListViewModel.showings.getValueBlocking()
        assertEquals(Status.SUCCESS, result!!.status)
        assertEquals(null, result.message)
        assertEquals(showings, result.data)
    }
}