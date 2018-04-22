package xyz.arnau.muvicat.cache.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import xyz.arnau.muvicat.cache.db.constants.DatabaseConstants.MOVIE_TABLE
import xyz.arnau.muvicat.data.model.Movie

@Dao
abstract class MovieDao {
    @Query("SELECT * FROM $MOVIE_TABLE")
    abstract fun getMovies(): LiveData<List<Movie>>

    @Query("DELETE FROM $MOVIE_TABLE")
    abstract fun clearMovies()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMovie(movie: Movie)

    @Query("SELECT * FROM $MOVIE_TABLE LIMIT 1")
    abstract fun isCached(): Boolean

}