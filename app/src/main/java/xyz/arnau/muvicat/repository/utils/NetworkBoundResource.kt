package xyz.arnau.muvicat.repository.utils

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import xyz.arnau.muvicat.repository.model.Resource
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.remote.model.ResponseStatus
import xyz.arnau.muvicat.utils.AppExecutors

abstract class NetworkBoundResource<ResultType, RequestType> @MainThread constructor(private val appExecutors: AppExecutors) {
    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        @Suppress("LeakingThis")
        val dbSource: LiveData<ResultType> = loadFromDb()
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
    private fun setValue(newValue: Resource<ResultType>) {
        if (!equals(result.value, newValue)) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse: LiveData<Response<RequestType>> = createCall()
        result.addSource(dbSource, { newData -> setValue(Resource.loading(newData)) })
        result.addSource(apiResponse, { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            when {
                (response?.type == ResponseStatus.SUCCESSFUL && response.body != null)
                        || response?.type == ResponseStatus.NOT_MODIFIED ->
                    appExecutors.diskIO().execute {
                        saveResponse(response)
                        appExecutors.mainThread().execute {
                            result.addSource(loadFromDb(), { newData ->
                                setValue(Resource.success(newData))
                            })
                        }
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

    protected abstract fun onFetchFailed()

    fun asLiveData(): LiveData<Resource<ResultType>> = result

    @WorkerThread
    protected abstract fun saveResponse(response: Response<RequestType>)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    @MainThread
    protected abstract fun createCall(): LiveData<Response<RequestType>>

    @Suppress("SuspiciousEqualsCombination")
    private fun equals(a: Any?, b: Any): Boolean {
        return a === b || a != null && a == b
    }
}