package xyz.arnau.muvicat.data

import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread
/*
abstract class GencatNetworkBoundResource<ResultType, RequestType> @MainThread
internal constructor(private val appExecutors: AppExecutors) {
    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        val dbSource = this.loadFromDb()
        result.addSource(dbSource) {
            result.removeSource(dbSource)
            if (shouldFetch()) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData -> setValue(Resource.success(newData)) }
            }
        }
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        //if (result.value != newValue) {
        result.setValue(newValue)
        //}
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { newData -> setValue(Resource.loading(newData)) }
        result.addSource<ApiResponse<RequestType>>(apiResponse) { response ->
            result.removeSource<ApiResponse<RequestType>>(apiResponse)
            result.removeSource(dbSource)

            if (response.isSuccessful()) {
                appExecutors.diskIO().execute {
                    saveCallResult(processResponse(response))
                    appExecutors.mainThread().execute {
                        // we specially request a new live data,
                        // otherwise we will get immediately last cached value,
                        // which may not be updated with latest results received from network.
                        result.addSource(
                            loadFromDb()
                        ) { newData -> setValue(Resource.success(newData)) }
                    }
                }
            } else {
                onFetchFailed()
                result.addSource(
                    dbSource
                ) { newData -> setValue(Resource.error(response.errorMessage, newData)) }
            }
        }
    }

    protected fun onFetchFailed() {}

    fun asLiveData(): LiveData<Resource<ResultType>> = result



    @MainThread
    protected abstract fun shouldFetch(): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    //@MainThread
    //protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>
}*/