@file:Suppress("DEPRECATION")

package xyz.arnau.muvicat.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import xyz.arnau.muvicat.data.utils.RepoPreferencesHelper
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
}