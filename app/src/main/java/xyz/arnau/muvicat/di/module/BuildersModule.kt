package xyz.arnau.muvicat.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import xyz.arnau.muvicat.MainActivity

@Module
abstract class BuildersModule {
    @ContributesAndroidInjector(modules = [AppModule::class])
    abstract fun bindActivity(): MainActivity
}