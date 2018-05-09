package xyz.arnau.muvicat.cache

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import xyz.arnau.muvicat.cache.dao.ShowingDao
import xyz.arnau.muvicat.data.model.Showing
import xyz.arnau.muvicat.data.test.ShowingFactory


@RunWith(JUnit4::class)
class ShowingCacheImplTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val showingDao = mock(ShowingDao::class.java)

    private val showingCacheImpl = ShowingCacheImpl(showingDao)

    @Test
    fun getShowingsReturnsData() {
        val showings = ShowingFactory.makeShowingList(5)
        val showingsLiveData = MutableLiveData<List<Showing>>()
        showingsLiveData.value = showings
        `when`(showingDao.getShowings()).thenReturn(showingsLiveData)
        val showingsFromCache = showingCacheImpl.getShowings()
        verify(showingDao).getShowings()
        assertEquals(showings, showingsFromCache.value)
    }

    @Test
    fun updateShowingsUpdateData() {
        val showings = ShowingFactory.makeShowingList(5)
        showingCacheImpl.updateShowings(showings)

        verify(showingDao).updateShowingDb(showings)
    }
}