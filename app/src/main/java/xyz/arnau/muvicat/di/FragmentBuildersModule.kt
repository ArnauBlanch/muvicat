package xyz.arnau.muvicat.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import xyz.arnau.muvicat.ui.cinema.CinemaListFragment
import xyz.arnau.muvicat.ui.movie.CinemaMovieListFragment
import xyz.arnau.muvicat.ui.movie.MovieListFragment
import xyz.arnau.muvicat.ui.showing.CinemaShowingListFragment
import xyz.arnau.muvicat.ui.showing.ShowingListFragment

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeMovieListFragment(): MovieListFragment

    @ContributesAndroidInjector
    abstract fun contributeCinemaListFragment(): CinemaListFragment

    @ContributesAndroidInjector
    abstract fun contributeShowingListFragment(): ShowingListFragment

    @ContributesAndroidInjector
    abstract fun contributeCinemaShowingListFragment(): CinemaShowingListFragment

    @ContributesAndroidInjector
    abstract fun contributeCinemaMovieListFragment(): CinemaMovieListFragment
}
