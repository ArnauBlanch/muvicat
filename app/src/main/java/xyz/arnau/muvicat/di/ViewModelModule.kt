package xyz.arnau.muvicat.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import xyz.arnau.muvicat.viewmodel.MuvicatViewModelFactory
import xyz.arnau.muvicat.viewmodel.cinema.CinemaListViewModel
import xyz.arnau.muvicat.viewmodel.cinema.CinemaViewModel
import xyz.arnau.muvicat.viewmodel.movie.MovieListViewModel
import xyz.arnau.muvicat.viewmodel.movie.MovieViewModel
import xyz.arnau.muvicat.viewmodel.showing.ShowingListViewModel
import javax.inject.Singleton

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MovieListViewModel::class)
    abstract fun bindMovieListViewModel(movieListViewModel: MovieListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MovieViewModel::class)
    abstract fun bindMovieViewModel(movieViewModel: MovieViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CinemaListViewModel::class)
    abstract fun bindCinemaListViewModel(cinemaListViewModel: CinemaListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CinemaViewModel::class)
    abstract fun bindCinemaViewModel(cinemaViewModel: CinemaViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ShowingListViewModel::class)
    abstract fun bindShowingListViewModel(showingListViewModel: ShowingListViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: MuvicatViewModelFactory): ViewModelProvider.Factory
}
