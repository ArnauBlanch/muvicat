package xyz.arnau.muvicat.cache.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "postal_codes", indices = [(Index(value = ["code"], name = "postalCode"))])
data class PostalCodeEntity(
    @PrimaryKey
    var code: Int,
    var latitude: Double,
    var longitude: Double
)