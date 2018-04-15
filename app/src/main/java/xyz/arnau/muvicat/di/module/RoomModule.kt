package xyz.arnau.muvicat.di.module

import android.app.Application
import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
import xyz.arnau.muvicat.data.db.AppDatabase
import javax.inject.Singleton

@Module
class RoomModule(application: Application) {
    private var appDatabase: AppDatabase = Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "muvicat-db"
    ).build()

    @Singleton
    @Provides
    fun providesDatabase(): AppDatabase = appDatabase

    @Singleton
    @Provides
    fun providesMovieDao(appDatabase: AppDatabase) = appDatabase.movieDao()
}