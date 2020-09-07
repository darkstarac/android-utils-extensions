package it.wazabit.dev.extension.di.modules

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import it.wazabit.dev.extension.ds.network.HttpClient
import it.wazabit.dev.extension.ds.network.PlaceHolderService
import it.wazabit.dev.extension.ds.network.RetrofitResponseAdapter
import it.wazabit.dev.extensions.datasource.ResponseAdapter
import it.wazabit.dev.extensions.datasource.SafeApiCall
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object Network {


    @Singleton
    @Provides
    fun providesRetrofitPlaceHolderService(
        @ApplicationContext context: Context
    ) : PlaceHolderService {

        val moshi = Moshi.Builder()
            // ... add your own JsonAdapters and factories ...
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(HttpClient.getInstance(context))
            .build()
            .create(PlaceHolderService::class.java)
    }

    @Singleton
    @Provides
    fun providesRetrofitSafeApiCaller() : SafeApiCall{
        return SafeApiCall(ResponseAdapter.build(RetrofitResponseAdapter()))
    }

}