package it.wazabit.dev.extensions.datasource

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class NetworkBoundResource<Persistence : Any, Remote : Any> @MainThread constructor() {

    private val result = MediatorLiveData<NetworkResponse<Persistence>>()

    init {
        result.value = NetworkResponse.Loading(null)
        @Suppress("LeakingThis")
        val dbSource = loadFromDb()
        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData ->
                    setValue(NetworkResponse.Success(newData))
                }
            }
        }
    }

    @MainThread
    private fun setValue(newValue: NetworkResponse<Persistence>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<Persistence>) = GlobalScope.launch(Dispatchers.Main) {
        val apiResponse = createCall()

        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
//        result.addSource(dbSource) { newData ->
//            setValue(NetworkResponse.Loading(newData))
//        }

        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            when (response) {
                is NetworkResponse.Success -> {
                        GlobalScope.launch(Dispatchers.IO) {
                            saveCallResult(processResponse(response))
                            GlobalScope.launch(Dispatchers.Main) {
                                result.addSource(loadFromDb()) { newData ->
                                    setValue(NetworkResponse.Success(newData))
                                }
                            }
                        }
                }
                is NetworkResponse.Error -> {
                    onFetchFailed()
                    result.addSource(dbSource) { _ ->
                        setValue(NetworkResponse.Error(response.error, response.exception))
                    }
                }
            }
        }
    }

    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<NetworkResponse<Persistence>>

    @WorkerThread
    protected open fun processResponse(response: NetworkResponse.Success<Remote>) = response.data

    @WorkerThread
    protected abstract fun saveCallResult(item: Remote)

    @MainThread
    protected abstract fun shouldFetch(data: Persistence?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<Persistence>

    @MainThread
    protected abstract suspend fun createCall(): LiveData<NetworkResponse<Remote>>
}