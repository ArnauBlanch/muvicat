package xyz.arnau.muvicat.remote.model

import xyz.arnau.muvicat.remote.DataUpdateCallback

data class Response<T>(
    val body: T?,
    val errorMessage: String?,
    val type: ResponseStatus,
    val callback: DataUpdateCallback?
)