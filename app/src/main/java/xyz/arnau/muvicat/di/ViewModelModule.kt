package xyz.arnau.muvicat.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import xyz.arnau.muvicat.viewmodel.MuvicatViewModelFactory
import xyz.arnau.muvicat.viewmodel.movie.MovieListViewModel

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MovieListViewModel::class)
    internal abstract fun bindMovieListViewModel(movieListViewModel: MovieListViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: MuvicatViewModelFactory): ViewModelProvider.Factory
}
