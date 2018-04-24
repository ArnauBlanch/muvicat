package xyz.arnau.muvicat.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import xyz.arnau.muvicat.ui.MainActivity

@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = [(FragmentBuildersModule::class)])
    abstract fun contributeMainActivity(): MainActivity
}