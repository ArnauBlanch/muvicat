package xyz.arnau.muvicat.cache.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.Showing
import java.util.*

@Dao
abstract class ShowingDao {
    @Query("SELECT * FROM showings ORDER BY date ASC")
    abstract fun getShowings(): LiveData<List<Showing>>

    @Query("DELETE FROM showings")
    abstract fun clearShowings()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertShowings(showings: List<Showing>)

    @Transaction
    open fun updateShowingDb(showings: List<Showing>) {
        clearShowings()
        insertShowings(showings)
    }
}