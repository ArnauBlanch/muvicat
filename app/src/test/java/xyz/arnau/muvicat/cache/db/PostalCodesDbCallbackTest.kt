package xyz.arnau.muvicat.cache.db

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.db.SupportSQLiteDatabase
import android.content.Context
import android.content.res.Resources
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.*
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.mockito.PowerMockito.`when`
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import xyz.arnau.muvicat.cache.dao.PostalCodeDao
import xyz.arnau.muvicat.cache.model.PostalCodeEntity
import xyz.arnau.muvicat.cache.utils.PostalCodeCsvReader
import xyz.arnau.muvicat.utils.InstantAppExecutors
import java.io.InputStream


@RunWith(PowerMockRunner::class)
@PrepareForTest(MuvicatDatabase::class)
class PostalCodesDbCallbackTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val context = mock(Context::class.java)
    private val resources = mock(Resources::class.java)
    private val db = mock(MuvicatDatabase::class.java)
    private val dao = mock(PostalCodeDao::class.java)
    private val sqlDb = mock(SupportSQLiteDatabase::class.java)
    private val stream = mock(InputStream::class.java)

    private val csvReader = mock(PostalCodeCsvReader::class.java)
    private val appExecutors = InstantAppExecutors()

    private val callback = PostalCodesDbCallback(context, appExecutors, csvReader)

    @Before
    fun setUp() {
        `when`(context.resources).thenReturn(resources)
        `when`(resources.openRawResource(anyInt())).thenReturn(stream)

        PowerMockito.mockStatic(MuvicatDatabase::class.java)
        PowerMockito.`when`(MuvicatDatabase.getInstance(context, appExecutors)).thenReturn(db)

        `when`(csvReader.readPostalCodeCsv(stream))
            .thenReturn(listOf(PostalCodeEntity(0, 0.0, 0.0)))
        `when`(db.postalCodeDao()).thenReturn(dao)
    }

    @Test
    fun testOnCreate() {
        callback.onCreate(sqlDb)

        verify(csvReader, times(8)).readPostalCodeCsv(stream)
        verify(dao).insertPostalCodes(postalCodeList(8))
    }

    @Test
    fun testOnOpenEmpty() {
        `when`(dao.isNotEmpty()).thenReturn(false)
        callback.onOpen(sqlDb)
        verify(csvReader, times(8)).readPostalCodeCsv(stream)
        verify(dao).insertPostalCodes(postalCodeList(8))
    }

    @Test
    fun testOnOpenNotEmpty() {
        `when`(dao.isNotEmpty()).thenReturn(true)
        callback.onOpen(sqlDb)
        verify(csvReader, never()).readPostalCodeCsv(stream)
        verify(dao, never()).insertPostalCodes(postalCodeList(8))
    }

    private fun postalCodeList(count: Int): List<PostalCodeEntity> {
        val postalCodeList = mutableListOf<PostalCodeEntity>()
        repeat(count) {
            postalCodeList.add(PostalCodeEntity(0, 0.0, 0.0))
        }
        return postalCodeList
    }
}