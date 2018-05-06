package xyz.arnau.muvicat.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(
    tableName = "cinemas",
    indices = [(Index(value = ["id"], name = "cinemaId"))]
)
data class Cinema(
    @PrimaryKey
    var id: Long,
    var name: String,
    var address: String,
    var town: String?,
    var region: String?,
    var province: String?,
    var postalCode: Int?
)