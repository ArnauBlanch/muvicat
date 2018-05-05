package xyz.arnau.muvicat.data.model

data class CinemaInfo(
    var id: Long,
    var name: String,
    var address: String,
    var town: String?,
    var region: String?,
    var province: String?,
    var latitude: Double?,
    var longitude: Double?
)