package xyz.arnau.muvicat.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "postal_codes", indices = [(Index(value = ["code"], name = "postalCode"))])
data class PostalCode(
    @PrimaryKey
    var code: Int,
    var latitude: Double,
    var longitude: Double
)