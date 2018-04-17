package xyz.arnau.muvicat.cache.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import xyz.arnau.muvicat.cache.dao.CachedMovieDao
import xyz.arnau.muvicat.cache.db.constants.DatabaseConstants.DATABASE_NAME
import xyz.arnau.muvicat.cache.model.CachedMovie

@Database(entities = [(CachedMovie::class)], version = 1)
@TypeConverters(DateTypeConverter::class)
abstract class MuvicatDatabase : RoomDatabase() {
    abstract fun cachedMoviesDao(): CachedMovieDao

    /*
    private var instance: MuvicatDatabase? = null

    private val sLock = Any()

    fun getInstance(context: Context): MuvicatDatabase {
        if (instance == null) {
            synchronized(sLock) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MuvicatDatabase::class.java, DATABASE_NAME
                    )
                        .build()
                }
                return instance!!
            }
        }
        return instance!!
    }
    */
}