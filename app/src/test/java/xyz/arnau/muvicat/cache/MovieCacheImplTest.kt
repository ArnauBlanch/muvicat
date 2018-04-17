package xyz.arnau.muvicat.cache

import android.arch.persistence.room.Room
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSystemClock
import xyz.arnau.muvicat.cache.db.MuvicatDatabase
import xyz.arnau.muvicat.cache.mapper.MovieEntityMapper
import xyz.arnau.muvicat.cache.model.CachedMovie
import xyz.arnau.muvicat.cache.test.MovieFactory
import xyz.arnau.muvicat.data.model.MovieEntity
import xyz.arnau.muvicat.data.repository.MovieCache


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [16])
class MovieCacheImplTest {
    private var muvicatDatabase = Room.inMemoryDatabaseBuilder(RuntimeEnvironment.application,
        MuvicatDatabase::class.java).allowMainThreadQueries().build()
    private var entityMapper = MovieEntityMapper()
    private var preferencesHelper: PreferencesHelper = mock(PreferencesHelper::class.java)

    private val databaseHelper = MovieCacheImpl(muvicatDatabase, entityMapper, preferencesHelper)

    @Before
    fun setUpMockito() {
        MockitoAnnotations.initMocks(this)
    }

    @After
    fun tearDownMockito() {
        Mockito.validateMockitoUsage()
    }

    @Test
    fun clearMoviesCompletes() {
        val testObserver = databaseHelper.clearMovies().test()
        testObserver.assertComplete()
    }

    @Test
    fun saveMoviesCompletes() {
        val movieEntities = MovieFactory.makeMovieEntityList(3)

        val testObserver = databaseHelper.saveMovies(movieEntities).test()
        testObserver.assertComplete()
    }

    @Test
    fun saveMoviesSavesData() {
        val countMovies = 3
        val movieEntities = MovieFactory.makeMovieEntityList(countMovies)

        databaseHelper.saveMovies(movieEntities).test()
        val savedMovies = getMovies()
        assertEquals(savedMovies, movieEntities.sortedWith(compareBy({ it.id }, { it.id })))
    }

    @Test
    fun getMoviesCompletes() {
        val testObserver = databaseHelper.getMovies().test()
        testObserver.assertComplete()
    }

    @Test
    fun getMoviesReturnsData() {
        val movieEntities = MovieFactory.makeMovieEntityList(3)
        insertMovies(movieEntities)

        val testObserver = databaseHelper.getMovies().test()
        testObserver.assertValue(movieEntities.sortedWith(compareBy({ it.id }, { it.id })))
    }

    @Test
    fun isCachedReturnsFalseIfNoMovies() {
        val testObserver = databaseHelper.isCached().test()
        testObserver.assertValue(false)
    }

    @Test
    fun isExpiredReturnsTrueIfExpired() {
        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.lastCacheTime)
            .thenReturn(currentTime - (MovieCacheImpl.EXPIRATION_TIME + 500))
        assertEquals(databaseHelper.isExpired(), true)
    }

    @Test
    fun isExpiredReturnsFalseIfNotExpired() {
        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.lastCacheTime)
            .thenReturn(currentTime - 5000)
        assertEquals(databaseHelper.isExpired(), false)
    }

    @Test
    fun isCachedReturnsTrueIfMoviesExist() {
        val movieEntities = MovieFactory.makeMovieEntityList(3)
        insertMovies(movieEntities)

        val testObserver = databaseHelper.isCached().test()
        testObserver.assertValue(true)
    }

    @Test
    fun setLastTimeCacheTest() {
        val lastTime = 150.toLong()

        databaseHelper.setLastCacheTime(lastTime)
        verify(preferencesHelper).lastCacheTime = lastTime
    }

    private fun getMovies(): List<MovieEntity> {
        val cachedMovies = muvicatDatabase.cachedMoviesDao().getMovies()
        val movieEntities = mutableListOf<MovieEntity>()
        cachedMovies.forEach {
            movieEntities.add(entityMapper.mapFromCached(it))
        }

        return movieEntities
    }

    private fun insertMovies(movieEntities: List<MovieEntity>) {
        val cachedMovies = mutableListOf<CachedMovie>()
        movieEntities.forEach {
            cachedMovies.add(entityMapper.mapToCached(it))
        }
        cachedMovies.forEach {
            muvicatDatabase.cachedMoviesDao().insertMovie(it)
        }
    }
}