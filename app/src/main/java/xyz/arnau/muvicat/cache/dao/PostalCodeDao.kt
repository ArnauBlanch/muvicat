package xyz.arnau.muvicat.cache.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import xyz.arnau.muvicat.cache.model.PostalCodeEntity

@Dao
abstract class PostalCodeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertPostalCodes(postalCodes: List<PostalCodeEntity>)

    @Query("SELECT * FROM postal_codes")
    abstract fun getPostalCodes(): List<PostalCodeEntity>

    @Query("SELECT * FROM postal_codes LIMIT 1")
    abstract fun isNotEmpty(): Boolean
}