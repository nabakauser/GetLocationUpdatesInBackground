package com.example.locationupdates.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_coordinates")
data class Location(

    @PrimaryKey(autoGenerate = true)
    val latitude: Double?,
    val longitude: Double?,

)