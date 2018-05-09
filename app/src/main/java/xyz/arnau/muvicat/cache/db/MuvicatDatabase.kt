package xyz.arnau.muvicat.cache.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import xyz.arnau.muvicat.AppExecutors
import xyz.arnau.muvicat.cache.dao.CinemaDao
import xyz.arnau.muvicat.cache.dao.MovieDao
import xyz.arnau.muvicat.cache.dao.PostalCodeDao
import xyz.arnau.muvicat.cache.dao.ShowingDao
import xyz.arnau.muvicat.cache.db.migrations.*
import xyz.arnau.muvicat.cache.utils.PostalCodeCsvReader
import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.PostalCode
import xyz.arnau.muvicat.data.model.Showing

@Database(entities = [Movie::class, Cinema::class, PostalCode::class, Showing::class], version = 4)
@TypeConverters(DateTypeConverter::class)
abstract class MuvicatDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun cinemaDao(): CinemaDao
    abstract fun postalCodeDao(): PostalCodeDao
    abstract fun showingDao(): ShowingDao

    companion object {
        private var INSTANCE: MuvicatDatabase? = null

        @Synchronized
        fun getInstance(context: Context, appExecutors: AppExecutors): MuvicatDatabase {
            if (INSTANCE == null) {
                INSTANCE = buildDatabase(context, appExecutors)
            }
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context, appExecutors: AppExecutors): MuvicatDatabase {
            return Room.databaseBuilder(context, MuvicatDatabase::class.java, "muvicat-db")
                .addMigrations(
                    DbMigration1to2,
                    DbMigration2to3,
                    DbMigration3to4
                )
                .addCallback(PostalCodesDbCallback(context, appExecutors, PostalCodeCsvReader()))
                .build()
        }
    }
}