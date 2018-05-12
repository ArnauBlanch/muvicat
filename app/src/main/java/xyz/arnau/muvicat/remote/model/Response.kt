package xyz.arnau.muvicat.remote.model

import xyz.arnau.muvicat.remote.DataUpdateCallback
import xyz.arnau.muvicat.remote.model.ResponseStatus.*

data class Response<T>(
    val body: T?,
    val errorMessage: String?,
    val type: ResponseStatus,
    val callback: DataUpdateCallback?
) {
    companion object {
        fun <T> successful(body: T?, callback: DataUpdateCallback?): Response<T> {
            return Response(body, null, SUCCESSFUL, callback)
        }

        fun <T> error(msg: String?, callback: DataUpdateCallback?): Response<T> {
            return Response(null, msg, ERROR, callback)
        }

        fun <T> notModified(callback: DataUpdateCallback?): Response<T> {
            return Response(null, null, NOT_MODIFIED, callback)
        }
    }
}