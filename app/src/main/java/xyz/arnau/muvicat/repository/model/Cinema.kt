package xyz.arnau.muvicat.repository.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore

@Entity
data class Cinema constructor(
    var id: Long,
    var name: String,
    var address: String,
    var town: String?,
    var region: String?,
    var province: String?,
    var latitude: Double?,
    var longitude: Double?,
    var numMovies: Int,
    var numShowings: Int
) {
    @Ignore
    var distance: Int? = null
}