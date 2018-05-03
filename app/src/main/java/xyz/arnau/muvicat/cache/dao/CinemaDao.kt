package xyz.arnau.muvicat.cache.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.data.model.Movie
import java.util.*

@Dao
abstract class CinemaDao {
    @Query("SELECT * FROM cinemas")
    abstract fun getCinemas(): LiveData<List<Cinema>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertCinemas(cinemas: List<Cinema>)

    @Query("DELETE FROM cinemas")
    abstract fun clearCinemas()

    @Transaction
    open fun updateCinemaDb(cinemas: List<Cinema>) {
        clearCinemas()
        insertCinemas(cinemas)
    }
}