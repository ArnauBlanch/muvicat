package xyz.arnau.muvicat.cache.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import xyz.arnau.muvicat.cache.model.CinemaEntity
import xyz.arnau.muvicat.data.model.Cinema

@Dao
abstract class CinemaDao {
    @Query("""
        SELECT c.id, c.name, c.address, c.town, c.region, c.province, pc.latitude, pc.longitude
        FROM cinemas c LEFT OUTER JOIN postal_codes pc ON c.postalCode != 0 AND c.postalCode = pc.code""")
    abstract fun getCinemas(): LiveData<List<Cinema>>

    @Query("""
        SELECT c.id, c.name, c.address, c.town, c.region, c.province, pc.latitude, pc.longitude
        FROM cinemas c LEFT OUTER JOIN postal_codes pc ON c.postalCode = pc.code
        WHERE id = :cinemaId""")
    abstract fun getCinema(cinemaId: Long): LiveData<Cinema>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertCinemas(cinemas: List<CinemaEntity>)

    @Query("DELETE FROM cinemas")
    abstract fun clearCinemas()

    @Transaction
    open fun updateCinemaDb(cinemas: List<CinemaEntity>) {
        clearCinemas()
        insertCinemas(cinemas)
    }
}