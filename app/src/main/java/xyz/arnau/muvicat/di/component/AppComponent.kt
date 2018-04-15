package xyz.arnau.muvicat.di.component

import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import xyz.arnau.muvicat.App
import xyz.arnau.muvicat.di.module.AppModule
import xyz.arnau.muvicat.di.module.BuildersModule
import xyz.arnau.muvicat.di.module.RoomModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    RoomModule::class,
    BuildersModule::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: App): Builder
        fun build(): AppComponent
    }
    fun inject(app: App)
}