package xyz.arnau.muvicat.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import xyz.arnau.muvicat.ui.MainActivity
import xyz.arnau.muvicat.ui.cinema.CinemaActivity
import xyz.arnau.muvicat.ui.movie.MovieActivity

@Suppress("unused")
@Module
abstract class ActivitiesModule {
    @ContributesAndroidInjector(modules = [(FragmentBuildersModule::class)])
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector()
    abstract fun contributeMovieActivity(): MovieActivity

    @ContributesAndroidInjector(modules = [(FragmentBuildersModule::class)])
    abstract fun contributeCinemaActivity(): CinemaActivity
}