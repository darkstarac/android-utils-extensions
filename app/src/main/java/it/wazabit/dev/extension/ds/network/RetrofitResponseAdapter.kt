package it.wazabit.dev.extension.ds.network

import com.squareup.moshi.JsonDataException
import it.wazabit.dev.extensions.datasource.NetworkError
import it.wazabit.dev.extensions.datasource.NetworkResponse
import it.wazabit.dev.extensions.datasource.ResponseAdapter
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber

class RetrofitResponseAdapter : ResponseAdapter<Response<*>> {

    override fun code(response: Response<*>): Int {
        return response.code()
    }

    override fun parseError(response: Response<*>): String {
        return response.errorBody().toString()
    }

    override fun <K : Any> isSuccessful(response: K): Boolean {
        Timber.d("Checking response $response")
        return if (response is Response<*>){
            //Need check if not type converter is used
            response.isSuccessful
        }else{
            true
        }
    }

    override fun parseException(exception: Throwable?): NetworkResponse.Error {
        return when(exception){
            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                errorBody?.let {
                    NetworkResponse.Error(
                        NetworkError.HttpError(exception.code(), it.string()),
                        exception
                    )
                } ?: NetworkResponse.Error(NetworkError.Unknown, exception)
            }
            is JsonDataException->{
                NetworkResponse.Error(
                    NetworkError.JsonParsingError(exception.message),
                    exception.cause
                )
            }
            else -> {
                NetworkResponse.Error(NetworkError.Unknown, exception)
            }
        }
    }

}