package it.wazabit.dev.extension.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import it.wazabit.dev.extension.ds.persistance.AppDataBase
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object Database {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) : AppDataBase{
        return Room.databaseBuilder(context,AppDataBase::class.java,"placeholder-database")
            .fallbackToDestructiveMigration()
            .build()
    }

}