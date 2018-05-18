package xyz.arnau.muvicat.cache.db.migrations

import android.arch.persistence.db.SupportSQLiteDatabase
import android.database.Cursor
import org.junit.Assert

object MigrationCinemaUtils {
    fun insertCinemas(db: SupportSQLiteDatabase) {
        db.execSQL(
            """INSERT INTO cinemas
                VALUES(1, "name1", "address1", "town1", "region1", "province1", 1)"""
        )
        db.execSQL(
            """INSERT INTO cinemas
                VALUES(2, "name2", "address2", null, null, null, null)"""
        )
    }

    fun checkCinemas(db: SupportSQLiteDatabase) {
        val cursor = db.query("SELECT * FROM cinemas ORDER BY id")
        Assert.assertEquals(2, cursor.count)
        cursor.moveToNext()
        assertCinema(
            cursor, 1, "name1", "address1", "town1",
            "region1", "province1", 1
        )
        cursor.moveToNext()
        assertCinema(
            cursor, 2, "name2", "address2", null,
            null, null, 0
        )
        cursor.close()
    }

    private fun assertCinema(
        cursor: Cursor, id: Int, name: String, address: String, town: String?,
        region: String?, province: String?, postalCode: Int?
    ) {
        Assert.assertEquals(id.toLong(), cursor.getLong(0))
        Assert.assertEquals(name, cursor.getString(1))
        Assert.assertEquals(address, cursor.getString(2))
        Assert.assertEquals(town, cursor.getString(3))
        Assert.assertEquals(region, cursor.getString(4))
        Assert.assertEquals(province, cursor.getString(5))
        Assert.assertEquals(postalCode, cursor.getInt(6))
    }
}