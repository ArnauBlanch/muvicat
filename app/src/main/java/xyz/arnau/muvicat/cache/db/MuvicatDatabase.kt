package xyz.arnau.muvicat.cache.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import xyz.arnau.muvicat.cache.dao.CinemaDao
import xyz.arnau.muvicat.cache.dao.MovieDao
import xyz.arnau.muvicat.cache.dao.PostalCodeDao
import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.PostalCode

@Database(entities = [Movie::class, Cinema::class, PostalCode::class], version = 3)
@TypeConverters(DateTypeConverter::class)
abstract class MuvicatDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun cinemaDao(): CinemaDao
    abstract fun postalCodeDao(): PostalCodeDao
}