@file:Suppress("DEPRECATION")

package xyz.arnau.muvicat.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import xyz.arnau.muvicat.repository.CinemaRepository
import xyz.arnau.muvicat.repository.MovieRepository
import xyz.arnau.muvicat.repository.ShowingRepository
import xyz.arnau.muvicat.repository.data.CinemaCache
import xyz.arnau.muvicat.repository.data.GencatRemote
import xyz.arnau.muvicat.repository.data.MovieCache
import xyz.arnau.muvicat.repository.data.ShowingCache
import xyz.arnau.muvicat.repository.utils.RepoPreferencesHelper
import xyz.arnau.muvicat.utils.AfterCountDownLatch
import xyz.arnau.muvicat.utils.AppExecutors
import xyz.arnau.muvicat.utils.BeforeCountDownLatch
import xyz.arnau.muvicat.utils.DateFormatter
import javax.inject.Singleton

@Module(includes = [RemoteModule::class, CacheModule::class, ViewModelModule::class])
class AppModule {
    @Singleton
    @Provides
    fun provideContext(application: Application): Context {
        return application
    }

    @Singleton
    @Provides
    fun provideDateFormatter(context: Context): DateFormatter {
        return DateFormatter(context)
    }

    @Singleton
    @Provides
    fun provideRepoPreferencesHelper(context: Context): RepoPreferencesHelper {
        return RepoPreferencesHelper(context)
    }

    @Singleton
    @Provides
    fun provideBeforeShowingsCountDownLatch(): BeforeCountDownLatch {
        return BeforeCountDownLatch(2)
    }

    @Singleton
    @Provides
    fun provideAfterShowingsCountDownLatch(): AfterCountDownLatch {
        return AfterCountDownLatch(1)
    }

    @Singleton
    @Provides
    fun provideMovieRepository(
        movieCache: MovieCache, gencatRemote: GencatRemote,
        appExecutors: AppExecutors, preferencesHelper: RepoPreferencesHelper,
        beforeLatch: BeforeCountDownLatch, afterLatch: AfterCountDownLatch
    ): MovieRepository {
        return MovieRepository(
            movieCache,
            gencatRemote,
            appExecutors,
            preferencesHelper,
            beforeLatch,
            afterLatch
        )
    }

    @Singleton
    @Provides
    fun provideCinemaRepository(
        cinemaCache: CinemaCache, gencatRemote: GencatRemote,
        appExecutors: AppExecutors, preferencesHelper: RepoPreferencesHelper,
        beforeLatch: BeforeCountDownLatch, afterLatch: AfterCountDownLatch
    ): CinemaRepository {
        return CinemaRepository(
            cinemaCache,
            gencatRemote,
            appExecutors,
            preferencesHelper,
            beforeLatch,
            afterLatch
        )
    }

    @Singleton
    @Provides
    fun provideShowingRepository(
        showingCache: ShowingCache, gencatRemote: GencatRemote,
        appExecutors: AppExecutors, preferencesHelper: RepoPreferencesHelper,
        beforeLatch: BeforeCountDownLatch, afterLatch: AfterCountDownLatch
    ): ShowingRepository {
        return ShowingRepository(
            showingCache,
            gencatRemote,
            appExecutors,
            preferencesHelper,
            beforeLatch,
            afterLatch
        )
    }
}