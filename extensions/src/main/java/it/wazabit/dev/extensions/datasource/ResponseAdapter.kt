package it.wazabit.dev.extensions.datasource


interface ResponseAdapter<T>{
    fun <K:Any>isSuccessful(response:K):Boolean
    fun code(response:T):Int
    fun parseError(response:T):String
    fun parseException(exception: Throwable?) : NetworkResponse.Error


     companion object{
         @Suppress("UNCHECKED_CAST")
         fun <K>build(adapter: K):ResponseAdapter<Any>{
             return adapter as ResponseAdapter<Any>
         }
     }
}
