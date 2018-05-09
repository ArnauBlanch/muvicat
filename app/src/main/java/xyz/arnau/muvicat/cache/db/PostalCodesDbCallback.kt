package xyz.arnau.muvicat.cache.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import xyz.arnau.muvicat.AppExecutors
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.cache.dao.PostalCodeDao
import xyz.arnau.muvicat.cache.utils.PostalCodeCsvReader
import xyz.arnau.muvicat.data.model.PostalCode
import javax.inject.Inject

class PostalCodesDbCallback(
    private val context: Context,
    private val appExecutors: AppExecutors,
    private val csvReader: PostalCodeCsvReader
) : RoomDatabase.Callback() {
    @Inject
    lateinit var muvicatDatabase: MuvicatDatabase

    override fun onCreate(db: SupportSQLiteDatabase) {

        appExecutors.diskIO().execute {
            initPostalCodes(muvicatDatabase.postalCodeDao())
        }
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        appExecutors.diskIO().execute {
            val postalCodeDao = muvicatDatabase.postalCodeDao()
            if (!postalCodeDao.isNotEmpty())
                initPostalCodes(postalCodeDao)
        }
    }

    private fun initPostalCodes(postalCodeDao: PostalCodeDao) {
        val postalCodes = mutableListOf<PostalCode>()
        postalCodes.addAll(csvReader.readPostalCodeCsv(context.resources.openRawResource(R.raw.cp_barcelona)))
        postalCodes.addAll(csvReader.readPostalCodeCsv(context.resources.openRawResource(R.raw.cp_girona)))
        postalCodes.addAll(csvReader.readPostalCodeCsv(context.resources.openRawResource(R.raw.cp_tarragona)))
        postalCodes.addAll(csvReader.readPostalCodeCsv(context.resources.openRawResource(R.raw.cp_lleida)))

        postalCodes.addAll(csvReader.readPostalCodeCsv(context.resources.openRawResource(R.raw.cp_balears)))

        postalCodes.addAll(csvReader.readPostalCodeCsv(context.resources.openRawResource(R.raw.cp_castello)))
        postalCodes.addAll(csvReader.readPostalCodeCsv(context.resources.openRawResource(R.raw.cp_valencia)))
        postalCodes.addAll(csvReader.readPostalCodeCsv(context.resources.openRawResource(R.raw.cp_alacant)))

        postalCodeDao.insertPostalCodes(postalCodes)
    }
}