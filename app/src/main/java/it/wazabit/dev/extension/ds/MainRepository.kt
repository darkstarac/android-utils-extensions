package it.wazabit.dev.extension.ds

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.wazabit.dev.extension.ds.network.PlaceHolderService
import it.wazabit.dev.extension.ds.persistance.AppDataBase
import it.wazabit.dev.extension.ds.persistance.entities.User
import it.wazabit.dev.extension.ui.network.NetworkFragment
import it.wazabit.dev.extensions.datasource.NetworkBoundResource
import it.wazabit.dev.extensions.datasource.NetworkResponse
import it.wazabit.dev.extensions.datasource.SafeApiCall
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val placeHolderService: PlaceHolderService,
    private val safeApiCall: SafeApiCall,
    private val database:AppDataBase
){



    val users:LiveData<List<User>> = database.userDao().index()

    init {
        Timber.d("Main repository init")
    }



    fun test(simulation: NetworkFragment.NetworkErrorSimulation?): LiveData<NetworkResponse<List<User>>> {
        return object : NetworkBoundResource<List<User>, List<User>>(){
            override fun saveCallResult(item: List<User>) {
                database.userDao().insertAll(item)
            }

            override fun shouldFetch(data: List<User>?) = true

            override fun loadFromDb(): LiveData<List<User>> {
                return database.userDao().index()
            }

            override suspend fun createCall(): LiveData<NetworkResponse<List<User>>> {
                val call = safeApiCall.execute {
                    val response = when(simulation){
                        NetworkFragment.NetworkErrorSimulation.NONE -> placeHolderService.users()
                        NetworkFragment.NetworkErrorSimulation.PARSING -> placeHolderService.usersInvalid()
                        NetworkFragment.NetworkErrorSimulation.NOT_FOUND -> placeHolderService.usersNotFound()
                        null ->  {
                            placeHolderService.users()
                        }
                    }
                    response
                }

                return MutableLiveData(call)
            }


        }.asLiveData()
    }

}