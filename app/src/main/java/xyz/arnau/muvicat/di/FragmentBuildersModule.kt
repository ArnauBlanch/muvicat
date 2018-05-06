package xyz.arnau.muvicat.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import xyz.arnau.muvicat.ui.cinema.CinemaListFragment
import xyz.arnau.muvicat.ui.movie.MovieListFragment

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeMovieListFragment(): MovieListFragment

    @ContributesAndroidInjector
    abstract fun contributeCinemaListFragment(): CinemaListFragment
}
