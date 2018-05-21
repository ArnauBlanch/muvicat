package xyz.arnau.muvicat.cache.db.migrations

import android.arch.persistence.db.SupportSQLiteDatabase
import android.database.Cursor
import junit.framework.TestCase.assertEquals

object MigrationPostalCodeUtils {
    fun insertPostalCodes3(db: SupportSQLiteDatabase) {
        db.execSQL("INSERT INTO postal_codes VALUES(111, 1.11, 1.11)")
        db.execSQL("INSERT INTO postal_codes VALUES(222, 2.22, 2.22)")
    }

    fun checkPostalCodes3to4(db: SupportSQLiteDatabase) {
        val cursor = db.query("SELECT * FROM postal_codes ORDER BY code")
        assertEquals(2, cursor.count)
        cursor.moveToNext()
        assertPostalCode(cursor, 111, 1.11, 1.11)
        cursor.moveToNext()
        assertPostalCode(cursor, 222, 2.22, 2.22)
        cursor.close()
    }

    private fun assertPostalCode(cursor: Cursor, code: Int, latitude: Double, longitude: Double) {
        assertEquals(code, cursor.getInt(0))
        assertEquals(latitude, cursor.getDouble(1))
        assertEquals(longitude, cursor.getDouble(2))
    }
}