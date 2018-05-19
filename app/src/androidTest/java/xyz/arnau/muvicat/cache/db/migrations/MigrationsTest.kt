package xyz.arnau.muvicat.cache.db.migrations

import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import android.arch.persistence.room.testing.MigrationTestHelper
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import xyz.arnau.muvicat.cache.db.MuvicatDatabase
import xyz.arnau.muvicat.cache.db.migrations.MigrationCinemaUtils.checkCinemas
import xyz.arnau.muvicat.cache.db.migrations.MigrationCinemaUtils.insertCinemas
import xyz.arnau.muvicat.cache.db.migrations.MigrationMovieUtils.checkMovies
import xyz.arnau.muvicat.cache.db.migrations.MigrationMovieUtils.checkMoviesOld
import xyz.arnau.muvicat.cache.db.migrations.MigrationMovieUtils.insertMoviesOld
import xyz.arnau.muvicat.cache.db.migrations.MigrationPostalCodeUtils.checkPostalCodes
import xyz.arnau.muvicat.cache.db.migrations.MigrationPostalCodeUtils.insertPostalCodes
import xyz.arnau.muvicat.cache.db.migrations.MigrationShowingUtils.checkShowings
import xyz.arnau.muvicat.cache.db.migrations.MigrationShowingUtils.insertShowings

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

        insertMoviesOld(db)
        db.close()

        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, DbMigration1to2)

        checkMoviesOld(db)
        db.close()
    }

    @Test
    fun migrate2To3() {
        var db = helper.createDatabase(TEST_DB, 2)

        insertMoviesOld(db)
        insertCinemas(db)
        db.close()

        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, DbMigration2to3)

        checkMoviesOld(db)
        checkCinemas(db)
        db.close()
    }


    @Test
    fun migrate3To4() {
        var db = helper.createDatabase(TEST_DB, 3)
        insertMoviesOld(db)
        insertCinemas(db)
        insertPostalCodes(db)
        db.close()

        db = helper.runMigrationsAndValidate(TEST_DB, 4, true, DbMigration3to4)

        checkMoviesOld(db)
        checkCinemas(db)
        checkPostalCodes(db)
    }

    @Test
    fun migrate4To5() {
        var db = helper.createDatabase(TEST_DB, 4)
        insertMoviesOld(db)
        insertCinemas(db)
        insertPostalCodes(db)
        insertShowings(db)
        db.close()

        db = helper.runMigrationsAndValidate(TEST_DB, 5, true, DbMigration4to5)

        checkMovies(db)
        checkCinemas(db)
        checkPostalCodes(db)
        checkShowings(db)
    }
}