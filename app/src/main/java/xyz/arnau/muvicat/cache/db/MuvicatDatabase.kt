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