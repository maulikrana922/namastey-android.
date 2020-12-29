package com.namastey.roomDB.dao

import androidx.room.*
import com.namastey.roomDB.entity.RecentLocations
import java.util.*

@Dao
interface RecentLocationsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addRecentLocations(recentLocations: RecentLocations)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addAllRecentLocations(recentLocationList: ArrayList<RecentLocations>)

    @Delete
    fun deleteRecentLocations(recentLocations: RecentLocations)

    @Query("SELECT * FROM recentLocations")
    fun getRecentLocations(): RecentLocations

    @Update
    fun updateRecentLocations(recentLocations: RecentLocations)

    // @Query(value = "SELECT * FROM recentLocations ORDER BY currentTime DESC LIMIT 0,5")
    @Query(value = "SELECT * FROM recentLocations ORDER BY currentTime DESC LIMIT 0,5")
    fun getAllRecentLocations(): List<RecentLocations>

    @Query(value = "SELECT * FROM recentLocations ORDER BY currentTime DESC LIMIT 1")
    fun getLastRecentLocations(): RecentLocations

    @Query("SELECT * FROM recentLocations WHERE id = :locationId")
    fun getExistingRecentLocations(locationId: Int): Boolean

    @Query("DELETE FROM recentLocations WHERE id = :locationId")
    fun deleteExistingLocations(locationId: Int)
}