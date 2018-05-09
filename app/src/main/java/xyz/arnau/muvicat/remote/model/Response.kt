package xyz.arnau.muvicat.remote.model

data class Response<T>(
    val body: T?,
    val errorMessage: String?,
    val type: ResponseStatus
)