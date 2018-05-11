package xyz.arnau.muvicat.cache.dao

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import xyz.arnau.muvicat.cache.db.MuvicatDatabase
import xyz.arnau.muvicat.data.test.PostalCodeEntityFactory


@RunWith(AndroidJUnit4::class)
class PostalCodeDaoTest {

    private var muvicatDatabase = Room.inMemoryDatabaseBuilder(
        InstrumentationRegistry.getContext(),
        MuvicatDatabase::class.java
    ).allowMainThreadQueries().build()

    @After
    fun closeDb() {
        muvicatDatabase.close()
    }

    @Test
    fun getPostalCodesRetrievesData() {
        val postalCodes = PostalCodeEntityFactory.makePostalCodeEntityList(4)

        muvicatDatabase.postalCodeDao().insertPostalCodes(postalCodes)

        val retrievedCodes = muvicatDatabase.postalCodeDao().getPostalCodes()
        assertEquals(
            postalCodes.sortedWith(compareBy({ it.code }, { it.code })),
            retrievedCodes
        )
    }

    @Test
    fun isNotEmptyReturnsTrueWhenDataExists() {
        val postalCodes = PostalCodeEntityFactory.makePostalCodeEntityList(4)

        muvicatDatabase.postalCodeDao().insertPostalCodes(postalCodes)

        assertEquals(
            true,
            muvicatDatabase.postalCodeDao().isNotEmpty()
        )
    }

    @Test
    fun isNotEmptyReturnsFalseWhenDataDoesNotExist() {
        assertEquals(
            false,
            muvicatDatabase.postalCodeDao().isNotEmpty()
        )
    }
}