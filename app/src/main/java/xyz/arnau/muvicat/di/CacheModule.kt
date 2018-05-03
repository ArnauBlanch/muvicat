package xyz.arnau.muvicat.di

import android.app.Application
import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
import xyz.arnau.muvicat.cache.CinemaCacheImpl
import xyz.arnau.muvicat.cache.MovieCacheImpl
import xyz.arnau.muvicat.cache.dao.CinemaDao
import xyz.arnau.muvicat.cache.dao.MovieDao
import xyz.arnau.muvicat.cache.db.Migration_1_2
import xyz.arnau.muvicat.cache.db.MuvicatDatabase
import xyz.arnau.muvicat.data.repository.CinemaCache
import xyz.arnau.muvicat.data.repository.MovieCache
import xyz.arnau.muvicat.data.utils.PreferencesHelper
import javax.inject.Singleton

@Module
class CacheModule {
    @Singleton
    @Provides
    fun provideDb(app: Application): MuvicatDatabase {
        return Room
            .databaseBuilder(app, MuvicatDatabase::class.java, "muvicat-db")
            .addMigrations(Migration_1_2)
            .build()
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
    fun provideMovieCache(movieDao: MovieDao, preferencesHelper: PreferencesHelper): MovieCache {
        return MovieCacheImpl(movieDao, preferencesHelper)
    }

    @Singleton
    @Provides
    fun provideCinemaCache(cinemaDao: CinemaDao, preferencesHelper: PreferencesHelper): CinemaCache {
        return CinemaCacheImpl(cinemaDao, preferencesHelper)
    }
}