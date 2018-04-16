package xyz.arnau.muvicat.cache.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import xyz.arnau.muvicat.cache.db.constants.DatabaseConstants.MOVIE_TABLE
import xyz.arnau.muvicat.cache.model.CachedMovie

@Dao
abstract class CachedMovieDao {
    @Query(value = "SELECT * FROM $MOVIE_TABLE")
    abstract fun getMovies(): List<CachedMovie>

    @Query(value = "DELETE FROM $MOVIE_TABLE")
    abstract fun clearMovies()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMovie(cachedMovie: CachedMovie)
}