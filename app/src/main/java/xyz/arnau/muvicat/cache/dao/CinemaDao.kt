package xyz.arnau.muvicat.cache.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.data.model.CinemaInfo

@Dao
abstract class CinemaDao {
    @Query("""
        SELECT c.id, c.name, c.address, c.town, c.region, c.province, pc.latitude, pc.longitude
        FROM cinemas c LEFT OUTER JOIN postal_codes pc ON c.postalCode != 0 AND c.postalCode = pc.code""")
    abstract fun getCinemas(): LiveData<List<CinemaInfo>>

    @Query("""
        SELECT c.id, c.name, c.address, c.town, c.region, c.province, pc.latitude, pc.longitude
        FROM cinemas c LEFT OUTER JOIN postal_codes pc ON c.postalCode = pc.code
        WHERE id = :cinemaId""")
    abstract fun getCinema(cinemaId: Long): LiveData<CinemaInfo>

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