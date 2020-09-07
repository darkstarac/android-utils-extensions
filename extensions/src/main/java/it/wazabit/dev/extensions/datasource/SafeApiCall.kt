package it.wazabit.dev.extensions.datasource

import java.io.IOException
import java.net.SocketTimeoutException

class SafeApiCall(val adapter:ResponseAdapter<Any>) {

    inline fun <T:Any> execute(block: () -> T): NetworkResponse<T> {

        val result = runCatching(block)
        
        return if (result.isSuccess) {
            val response = result.getOrNull()!!
            manageSuccess(response)
        } else {
            val exception = result.exceptionOrNull()
            exception?.printStackTrace()
            manageException(exception)
        }
    }

    fun <T:Any>manageException(exception:Throwable?) :NetworkResponse<T>{
        return when(exception){
            is SocketTimeoutException -> NetworkResponse.Error(NetworkError.Timeout, exception)
            is IOException -> NetworkResponse.Error(NetworkError.IOError, exception)
            else -> adapter.parseException(exception)
        }
    }

    fun <T:Any>manageSuccess(response:T) :NetworkResponse<T>{
        return with(adapter){
            if (isSuccessful(response)) {
                NetworkResponse.Success(response)
            }else {
                NetworkResponse.Error(
                    NetworkError.HttpError(
                        code(response),
                        parseError(response)
                    ), Exception("Invalid response")
                )
            }
        }
    }

}