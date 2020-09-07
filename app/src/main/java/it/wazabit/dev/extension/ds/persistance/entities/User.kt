package it.wazabit.dev.extension.ds.persistance.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    val id:Int,
    val name:String,
    val username:String,
    val email:String,
    @Embedded
    val address: Address
){


    data class Address(
        val street:String,
        val suite:String,
        val city:String,
        val zipcode:String,
    )
}