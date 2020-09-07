package it.wazabit.dev.extension.ds.persistance

import androidx.room.Database
import androidx.room.RoomDatabase
import it.wazabit.dev.extension.ds.persistance.daos.UserDao
import it.wazabit.dev.extension.ds.persistance.entities.User


@Database(entities = [User::class],version = 2)
abstract class AppDataBase  : RoomDatabase(){
    abstract fun userDao(): UserDao
}