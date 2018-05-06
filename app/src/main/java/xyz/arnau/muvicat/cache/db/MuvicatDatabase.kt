package xyz.arnau.muvicat.cache.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import xyz.arnau.muvicat.AppExecutors
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.cache.dao.CinemaDao
import xyz.arnau.muvicat.cache.dao.MovieDao
import xyz.arnau.muvicat.cache.dao.PostalCodeDao
import xyz.arnau.muvicat.cache.utils.PostalCodeCsvReader
import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.PostalCode

@Database(entities = [Movie::class, Cinema::class, PostalCode::class], version = 3)
@TypeConverters(DateTypeConverter::class)
abstract class MuvicatDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun cinemaDao(): CinemaDao
    abstract fun postalCodeDao(): PostalCodeDao

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
                .addMigrations(Migration_1_2)
                .addCallback(object: RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        appExecutors.diskIO().execute {
                            initPostalCodes(getInstance(context, appExecutors).postalCodeDao())
                        }
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        appExecutors.diskIO().execute {
                            val postalCodeDao = getInstance(context, appExecutors).postalCodeDao()
                            if (!postalCodeDao.isNotEmpty())
                                initPostalCodes(postalCodeDao)
                        }
                    }

                    private fun initPostalCodes(postalCodeDao: PostalCodeDao) {
                        val postalCodes = mutableListOf<PostalCode>()
                        postalCodes.addAll(
                            PostalCodeCsvReader
                                .readPostalCodeCsv(context.resources.openRawResource(R.raw.cp_barcelona))
                        )
                        postalCodes.addAll(
                            PostalCodeCsvReader
                                .readPostalCodeCsv(context.resources.openRawResource(R.raw.cp_girona))
                        )
                        postalCodes.addAll(
                            PostalCodeCsvReader
                                .readPostalCodeCsv(context.resources.openRawResource(R.raw.cp_tarragona))
                        )
                        postalCodes.addAll(
                            PostalCodeCsvReader
                                .readPostalCodeCsv(context.resources.openRawResource(R.raw.cp_lleida))
                        )

                        postalCodeDao.insertPostalCodes(postalCodes)
                    }
                })
                .build()
        }
    }
}