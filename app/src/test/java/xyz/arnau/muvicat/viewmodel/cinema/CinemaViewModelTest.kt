package xyz.arnau.muvicat.viewmodel.cinema

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import xyz.arnau.muvicat.data.CinemaRepository
import xyz.arnau.muvicat.data.model.CinemaInfo
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.model.Status
import xyz.arnau.muvicat.data.test.CinemaInfoFactory
import xyz.arnau.muvicat.utils.getValueBlocking

@RunWith(JUnit4::class)
class CinemaViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val cinemaRepository = mock(CinemaRepository::class.java)
    private val cinemaViewModel = CinemaViewModel(cinemaRepository)

    @Test
    fun getCinemaReturnsLiveData() {
        val cinema = CinemaInfoFactory.makeCinemaInfo()
        cinemaViewModel.setId(cinema.id)

        val cinemaLiveData = MutableLiveData<Resource<CinemaInfo>>()
        `when`(cinemaRepository.getCinema(cinema.id)).thenReturn(cinemaLiveData)
        cinemaLiveData.postValue(Resource.success(cinema))

        val result = cinemaViewModel.cinema.getValueBlocking()
        assertEquals(Status.SUCCESS, result!!.status)
        assertEquals(null, result.message)
        assertEquals(cinema, result.data)
    }
}