package xyz.arnau.muvicat.data.utils

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import xyz.arnau.muvicat.AppExecutors
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.remote.model.ResponseStatus

abstract class NetworkBoundResource<T> @MainThread constructor(private val appExecutors: AppExecutors) {
    private val result = MediatorLiveData<Resource<T>>()

    init {
        result.value = Resource.loading(null)
        val dbSource: LiveData<T> = loadFromDb()
        result.addSource(dbSource, { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource, { newData -> setValue(Resource.success(newData)) })
            }
        })
    }

    @MainThread
    private fun setValue(newValue: Resource<T>) {
        if (!equals(result.value, newValue)) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<T>) {
        val apiResponse: LiveData<Response<T>> = createCall()
        result.addSource(dbSource, { newData -> setValue(Resource.loading(newData)) })
        result.addSource(apiResponse, { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            when {
                response?.type == ResponseStatus.SUCCESSFUL ->
                    appExecutors.diskIO().execute({
                        saveResponse(response)
                        appExecutors.mainThread().execute({
                            result.addSource(loadFromDb(), { newData ->
                                setValue(Resource.success(newData))
                            })
                        })
                    })
                response?.type == ResponseStatus.NOT_MODIFIED -> {
                    saveResponse(response)
                    result.addSource(dbSource, { newData -> setValue(Resource.success(newData)) })
                }
                else -> {
                    onFetchFailed()
                    result.addSource(dbSource, { newData ->
                        setValue(Resource.error(response?.errorMessage, newData))
                    })
                }
            }
        })
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun onFetchFailed() {
    }

    fun asLiveData(): LiveData<Resource<T>> = result

    @WorkerThread
    protected abstract fun saveResponse(response: Response<T>)

    @MainThread
    protected abstract fun shouldFetch(data: T?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<T>

    @MainThread
    protected abstract fun createCall(): LiveData<Response<T>>

    private fun equals(a: Any?, b: Any): Boolean {
        @Suppress("SuspiciousEqualsCombination")
        return a === b || a != null && a == b
    }
}