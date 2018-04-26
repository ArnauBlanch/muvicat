package xyz.arnau.muvicat.cache.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import xyz.arnau.muvicat.cache.dao.MovieDao
import xyz.arnau.muvicat.data.model.Movie

@Database(entities = [(Movie::class)], version = 1)
@TypeConverters(DateTypeConverter::class)
abstract class MuvicatDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}