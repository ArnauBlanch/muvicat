package xyz.arnau.muvicat.di

import dagger.Module
import dagger.Provides
import xyz.arnau.muvicat.ui.cinema.CinemaListAdapter
import javax.inject.Singleton

@Suppress("unused")
@Module
abstract class AdapterModule {
    @Singleton
    @Provides
    fun provideCinemaListAdapter(): CinemaListAdapter? {
        return CinemaListAdapter()
    }
}