package it.wazabit.dev.extension.ds.persistance.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import it.wazabit.dev.extension.ds.persistance.entities.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun index(): LiveData<List<User>>

    @Insert(onConflict = REPLACE)
    fun save(user: User)

    @Insert(onConflict = REPLACE)
    fun insertAll(users: List<User>)

    @Delete
    fun delete(user: User)
}