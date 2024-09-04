package com.example.locationupdates.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // .IGNORE -> ignores the user if already present -> .REPLACE -> replaces old data
    fun addLocation(location: Location)

    @Query("SELECT * FROM location_coordinates")
    fun getLocation(): LiveData<List<Location>>

}