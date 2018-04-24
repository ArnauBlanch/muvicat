package xyz.arnau.muvicat.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import xyz.arnau.muvicat.ui.movielist.MovieListFragment

@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeMovieListFragment(): MovieListFragment
}
