package xyz.arnau.muvicat.utils

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.remote.model.ResponseStatus


class ApiUtil {
    companion object {
        fun <T> successCall(data: T): LiveData<Response<T>> {
            return createCall(Response(data, null, ResponseStatus.SUCCESSFUL, null))
        }

        fun <T> createCall(response: Response<T>): LiveData<Response<T>> {
            val data = MutableLiveData<Response<T>>()
            data.value = response
            return data
        }
    }
}