package xyz.arnau.muvicat.cache.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import xyz.arnau.muvicat.utils.AppExecutors
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.cache.dao.PostalCodeDao
import xyz.arnau.muvicat.cache.model.PostalCodeEntity
import xyz.arnau.muvicat.cache.utils.PostalCodeCsvReader
import javax.inject.Inject

class PostalCodesDbCallback @Inject constructor(
    private val context: Context,
    private val appExecutors: AppExecutors,
    private val csvReader: PostalCodeCsvReader
) : RoomDatabase.Callback() {

    fun test(context: Context, appExecutors: AppExecutors) {
        MuvicatDatabase.getInstance(context, appExecutors)
    }


    override fun onCreate(db: SupportSQLiteDatabase) {
        appExecutors.diskIO().execute {
            initPostalCodes(MuvicatDatabase.getInstance(context, appExecutors).postalCodeDao())
        }
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        appExecutors.diskIO().execute {
            val postalCodeDao = MuvicatDatabase.getInstance(context, appExecutors).postalCodeDao()
            if (!postalCodeDao.isNotEmpty())
                initPostalCodes(postalCodeDao)
        }
    }


    private fun initPostalCodes(postalCodeDao: PostalCodeDao) {
        val postalCodes = mutableListOf<PostalCodeEntity>()
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