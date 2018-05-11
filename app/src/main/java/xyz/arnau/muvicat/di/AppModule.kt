@file:Suppress("DEPRECATION")

package xyz.arnau.muvicat.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import junit.framework.Test
import xyz.arnau.muvicat.utils.AppExecutors
import xyz.arnau.muvicat.data.CinemaRepository
import xyz.arnau.muvicat.data.MovieRepository
import xyz.arnau.muvicat.data.ShowingRepository
import xyz.arnau.muvicat.data.repository.CinemaCache
import xyz.arnau.muvicat.data.repository.GencatRemote
import xyz.arnau.muvicat.data.repository.MovieCache
import xyz.arnau.muvicat.data.repository.ShowingCache
import xyz.arnau.muvicat.data.utils.RepoPreferencesHelper
import xyz.arnau.muvicat.utils.DateFormatter
import java.util.concurrent.CountDownLatch
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
    fun provideSyncCountDownLatch(): CountDownLatch {
        return CountDownLatch(2)
    }

    @Singleton
    @Provides
    fun provideMovieRepository(movieCache: MovieCache, gencatRemote: GencatRemote,
                               appExecutors: AppExecutors, preferencesHelper: RepoPreferencesHelper,
                               countDownLatch: CountDownLatch): MovieRepository {
        return MovieRepository(movieCache, gencatRemote, appExecutors, preferencesHelper, countDownLatch)
    }

    @Singleton
    @Provides
    fun provideCinemaRepository(cinemaCache: CinemaCache, gencatRemote: GencatRemote,
                                appExecutors: AppExecutors, preferencesHelper: RepoPreferencesHelper,
                                countDownLatch: CountDownLatch): CinemaRepository {
        return CinemaRepository(cinemaCache, gencatRemote, appExecutors, preferencesHelper, countDownLatch)
    }

    @Singleton
    @Provides
    fun provideShowingRepository(showingCache: ShowingCache, gencatRemote: GencatRemote,
                                 appExecutors: AppExecutors, preferencesHelper: RepoPreferencesHelper,
                                 countDownLatch: CountDownLatch): ShowingRepository {
        return ShowingRepository(showingCache, gencatRemote, appExecutors, preferencesHelper, countDownLatch)
    }
}