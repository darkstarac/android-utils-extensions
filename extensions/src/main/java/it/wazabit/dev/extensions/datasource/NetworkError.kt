package it.wazabit.dev.extensions.datasource

sealed class NetworkError {
    class HttpError(val httpCode: Int,val message: String) : NetworkError()
    class JsonParsingError(val message: String?) : NetworkError()
    object Timeout : NetworkError()
    object Unknown : NetworkError()
    object IOError : NetworkError()
}