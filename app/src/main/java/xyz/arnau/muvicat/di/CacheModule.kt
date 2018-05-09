package xyz.arnau.muvicat.di

import android.app.Application
import dagger.Module
import dagger.Provides
import xyz.arnau.muvicat.AppExecutors
import xyz.arnau.muvicat.cache.CinemaCacheImpl
import xyz.arnau.muvicat.cache.MovieCacheImpl
import xyz.arnau.muvicat.cache.ShowingCacheImpl
import xyz.arnau.muvicat.cache.dao.CinemaDao
import xyz.arnau.muvicat.cache.dao.MovieDao
import xyz.arnau.muvicat.cache.dao.ShowingDao
import xyz.arnau.muvicat.cache.db.MuvicatDatabase
import xyz.arnau.muvicat.data.repository.CinemaCache
import xyz.arnau.muvicat.data.repository.MovieCache
import xyz.arnau.muvicat.data.repository.ShowingCache
import javax.inject.Singleton

@Module
class CacheModule {
    @Singleton
    @Provides
    fun provideDb(app: Application, appExecutors: AppExecutors): MuvicatDatabase {
        return MuvicatDatabase.getInstance(app, appExecutors)
    }

    @Singleton
    @Provides
    fun provideMovieDao(db: MuvicatDatabase): MovieDao {
        return db.movieDao()
    }

    @Singleton
    @Provides
    fun provideCinemaDao(db: MuvicatDatabase): CinemaDao {
        return db.cinemaDao()
    }

    @Singleton
    @Provides
    fun provideShowingDao(db: MuvicatDatabase): ShowingDao {
        return db.showingDao()
    }

    @Singleton
    @Provides
    fun provideMovieCache(movieDao: MovieDao): MovieCache {
        return MovieCacheImpl(movieDao)
    }

    @Singleton
    @Provides
    fun provideCinemaCache(cinemaDao: CinemaDao): CinemaCache {
        return CinemaCacheImpl(cinemaDao)
    }

    @Singleton
    @Provides
    fun provideShowingCache(showingDao: ShowingDao): ShowingCache {
        return ShowingCacheImpl(showingDao)
    }
}