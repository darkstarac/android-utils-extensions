package it.wazabit.dev.extensions.datasource

/**
 * A generic class that contains data and status about loading this data.
 *
 * @param T success response data type.
 */
sealed class NetworkResponse<out T : Any> {

    data class Loading<out T : Any>(val data: T?) : NetworkResponse<T>()
    data class Success<out T : Any>(val data: T) : NetworkResponse<T>()
    data class Error(val error: NetworkError, val exception: Throwable?) : NetworkResponse<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Loading<*> -> "Loading[data=$data]"
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }

    companion object{
        fun <T:Any> validate(vararg responses: NetworkResponse<T>): Boolean {
            var valid = true
            responses.forEach {
                if (it is Error) valid = false
                return@forEach
            }
            return valid
        }
    }
}