package xyz.arnau.muvicat.data.model

import android.arch.persistence.room.Ignore

data class CinemaInfo constructor(
    var id: Long,
    var name: String,
    var address: String,
    var town: String?,
    var region: String?,
    var province: String?,
    var latitude: Double?,
    var longitude: Double?) {
    @Ignore
    var distance: Int? = null
}