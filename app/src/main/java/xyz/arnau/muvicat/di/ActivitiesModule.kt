package xyz.arnau.muvicat.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import xyz.arnau.muvicat.ui.MainActivity
import xyz.arnau.muvicat.ui.PrivacyPolicyActivity
import xyz.arnau.muvicat.ui.SplashActivity
import xyz.arnau.muvicat.ui.cinema.CinemaActivity
import xyz.arnau.muvicat.ui.home.AboutActivity
import xyz.arnau.muvicat.ui.movie.MovieActivity

@Suppress("unused")
@Module
abstract class ActivitiesModule {
    @ContributesAndroidInjector(modules = [(FragmentBuildersModule::class)])
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector()
    abstract fun contributeMovieActivity(): MovieActivity

    @ContributesAndroidInjector
    abstract fun contributeAboutActivity(): AboutActivity

    @ContributesAndroidInjector()
    abstract fun contributePrivacyPolicyActivity(): PrivacyPolicyActivity

    @ContributesAndroidInjector()
    abstract fun contributeSplashActivity(): SplashActivity

    @ContributesAndroidInjector(modules = [(FragmentBuildersModule::class)])
    abstract fun contributeCinemaActivity(): CinemaActivity
}