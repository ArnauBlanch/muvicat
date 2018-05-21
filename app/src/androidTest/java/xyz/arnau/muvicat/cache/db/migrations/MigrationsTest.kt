package xyz.arnau.muvicat.cache.db.migrations

import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import android.arch.persistence.room.testing.MigrationTestHelper
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import xyz.arnau.muvicat.cache.db.MuvicatDatabase
import xyz.arnau.muvicat.cache.db.migrations.MigrationCinemaUtils.checkCinemasVersion2to3
import xyz.arnau.muvicat.cache.db.migrations.MigrationCinemaUtils.insertCinemasVersion2
import xyz.arnau.muvicat.cache.db.migrations.MigrationMovieUtils.checkMoviesVersion1to2
import xyz.arnau.muvicat.cache.db.migrations.MigrationMovieUtils.checkMoviesVersion5to6
import xyz.arnau.muvicat.cache.db.migrations.MigrationMovieUtils.insertMoviesVersion1
import xyz.arnau.muvicat.cache.db.migrations.MigrationMovieUtils.insertMoviesVersion5
import xyz.arnau.muvicat.cache.db.migrations.MigrationPostalCodeUtils.checkPostalCodes3to4
import xyz.arnau.muvicat.cache.db.migrations.MigrationPostalCodeUtils.insertPostalCodes3
import xyz.arnau.muvicat.cache.db.migrations.MigrationShowingUtils.checkShowings4to5
import xyz.arnau.muvicat.cache.db.migrations.MigrationShowingUtils.insertShowings4

@RunWith(AndroidJUnit4::class)
class MigrationsTest {
    companion object {
        private const val TEST_DB = "migration-test"
    }

    @get:Rule
    private val helper =
        MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            MuvicatDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory()
            )

    @Test
    fun migrate1To2() {
        var db = helper.createDatabase(TEST_DB, 1)

        insertMoviesVersion1(db)
        db.close()

        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, DbMigration1to2)

        checkMoviesVersion1to2(db)
        db.close()
    }

    @Test
    fun migrate2To3() {
        var db = helper.createDatabase(TEST_DB, 2)

        insertMoviesVersion1(db)
        insertCinemasVersion2(db)
        db.close()

        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, DbMigration2to3)

        checkMoviesVersion1to2(db)
        checkCinemasVersion2to3(db)
        db.close()
    }


    @Test
    fun migrate3To4() {
        var db = helper.createDatabase(TEST_DB, 3)
        insertMoviesVersion1(db)
        insertCinemasVersion2(db)
        insertPostalCodes3(db)
        db.close()

        db = helper.runMigrationsAndValidate(TEST_DB, 4, true, DbMigration3to4)

        checkMoviesVersion1to2(db)
        checkCinemasVersion2to3(db)
        checkPostalCodes3to4(db)
    }

    @Test
    fun migrate4To5() {
        var db = helper.createDatabase(TEST_DB, 4)
        insertMoviesVersion1(db)
        insertCinemasVersion2(db)
        insertPostalCodes3(db)
        insertShowings4(db)
        db.close()

        db = helper.runMigrationsAndValidate(TEST_DB, 5, true, DbMigration4to5)

        checkMoviesVersion1to2(db)
        checkCinemasVersion2to3(db)
        checkPostalCodes3to4(db)
        checkShowings4to5(db)
    }

    @Test
    fun migrate5To6() {
        var db = helper.createDatabase(TEST_DB, 5)
        insertMoviesVersion5(db)
        insertCinemasVersion2(db)
        insertPostalCodes3(db)
        insertShowings4(db)
        db.close()

        db = helper.runMigrationsAndValidate(TEST_DB, 6, true, DbMigration5to6)

        checkMoviesVersion5to6(db)
        checkCinemasVersion2to3(db)
        checkPostalCodes3to4(db)
        checkShowings4to5(db)
    }
}