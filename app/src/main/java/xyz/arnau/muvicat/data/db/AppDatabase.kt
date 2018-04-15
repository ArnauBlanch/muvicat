package xyz.arnau.muvicat.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import xyz.arnau.muvicat.model.Movie

@Database(entities = [(Movie::class)], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun movieDao(): MovieDao
}