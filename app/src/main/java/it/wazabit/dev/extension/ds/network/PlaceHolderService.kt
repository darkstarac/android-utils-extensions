package it.wazabit.dev.extension.ds.network

import it.wazabit.dev.extension.ds.persistance.entities.User
import retrofit2.http.GET

interface PlaceHolderService{

    @GET("users/")
    suspend fun users() : List<User>

    @GET("users_404/")
    suspend fun usersNotFound() : List<User>

    @GET("posts/")
    suspend fun usersInvalid() : List<User>

}