/*package xyz.arnau.muvicat.data

import android.arch.persistence.room.Room
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import xyz.arnau.muvicat.cache.MovieCacheImpl
import xyz.arnau.muvicat.cache.PreferencesHelper
import xyz.arnau.muvicat.cache.db.MuvicatDatabase
import xyz.arnau.muvicat.cache.db.constants.DatabaseConstants
import xyz.arnau.muvicat.cache.mapper.CachedMovieEntityMapper
import xyz.arnau.muvicat.data.repository.MovieRepository
import xyz.arnau.muvicat.remote.GencatRemoteImpl
import xyz.arnau.muvicat.remote.mapper.GencatMovieEntityMapper
import xyz.arnau.muvicat.remote.service.GencatServiceFactory

@RunWith(RobolectricTestRunner::class)
class Test1 {
    private var db = Room.databaseBuilder(
        RuntimeEnvironment.application,
        MuvicatDatabase::class.java, DatabaseConstants.DATABASE_NAME
    ).build()
    private var dbEntityMapper = CachedMovieEntityMapper()
    private var preferencesHelper = PreferencesHelper(RuntimeEnvironment.application)
    private var movieCache = MovieCacheImpl(db, dbEntityMapper, preferencesHelper)

    private var gencatEntityMapper = GencatMovieEntityMapper()
    private var gencatService = GencatServiceFactory.makeGencatService(true)
    private var gencatRemote = GencatRemoteImpl(gencatService, gencatEntityMapper)

    private var movieRepo = MovieRepository(movieCache, gencatRemote)

    @Test
    fun test() {
        movieCache.getMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { print("HELLO!") }

        while (true) {}
    }

    @Test
    fun testRetrofit() {
        doAsync {
            val test = gencatRemote.getMovies()
            print(test)
        }
        while (true) {}
    }
}*/