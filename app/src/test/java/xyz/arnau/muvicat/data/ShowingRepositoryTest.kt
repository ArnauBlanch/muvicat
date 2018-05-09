package xyz.arnau.muvicat.data

import android.arch.core.executor.testing.InstantTaskExecutorRule
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import xyz.arnau.muvicat.AppExecutors
import xyz.arnau.muvicat.data.repository.GencatRemote
import xyz.arnau.muvicat.data.repository.ShowingCache
import xyz.arnau.muvicat.data.utils.RepoPreferencesHelper
import xyz.arnau.muvicat.utils.InstantAppExecutors

@RunWith(JUnit4::class)
class ShowingRepositoryTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val showingCache: ShowingCache = mock(ShowingCache::class.java)
    private val gencatRemote: GencatRemote = mock(GencatRemote::class.java)
    private val appExecutors: AppExecutors = InstantAppExecutors()
    private val preferencesHelper: RepoPreferencesHelper = mock(RepoPreferencesHelper::class.java)

    private val showingRepository =
        ShowingRepository(showingCache, gencatRemote, appExecutors, preferencesHelper)


    @Test
    fun hasExpiredReturnsTrueIfExpired() {
        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.showingslastUpdateTime)
            .thenReturn(currentTime - (ShowingRepository.EXPIRATION_TIME + 500))
        TestCase.assertEquals(true, showingRepository.hasExpired())
    }

    @Test
    fun isExpiredReturnsFalseIfNotExpired() {
        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.showingslastUpdateTime).thenReturn(currentTime - 5000)
        TestCase.assertEquals(false, showingRepository.hasExpired())
    }

    @Test
    fun companionObjectTest() {
        TestCase.assertEquals((3 * 60 * 60 * 1000).toLong(), ShowingRepository.EXPIRATION_TIME)
    }
}