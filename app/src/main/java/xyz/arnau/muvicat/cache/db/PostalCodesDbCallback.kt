package xyz.arnau.muvicat.cache.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import xyz.arnau.muvicat.AppExecutors
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.cache.dao.PostalCodeDao
import xyz.arnau.muvicat.cache.utils.PostalCodeCsvReader
import xyz.arnau.muvicat.data.model.PostalCode

class PostalCodesDbCallback(private val context: Context, private val appExecutors: AppExecutors)
    : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        appExecutors.diskIO().execute {
            initPostalCodes(MuvicatDatabase.getInstance(context, appExecutors).postalCodeDao())
        }
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        appExecutors.diskIO().execute {
            val postalCodeDao = MuvicatDatabase.getInstance(context, appExecutors).postalCodeDao()
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

        postalCodes.addAll(
            PostalCodeCsvReader
                .readPostalCodeCsv(context.resources.openRawResource(R.raw.cp_balears))
        )

        postalCodes.addAll(
            PostalCodeCsvReader
                .readPostalCodeCsv(context.resources.openRawResource(R.raw.cp_castello))
        )
        postalCodes.addAll(
            PostalCodeCsvReader
                .readPostalCodeCsv(context.resources.openRawResource(R.raw.cp_valencia))
        )
        postalCodes.addAll(
            PostalCodeCsvReader
                .readPostalCodeCsv(context.resources.openRawResource(R.raw.cp_alacant))
        )

        postalCodeDao.insertPostalCodes(postalCodes)
    }
}