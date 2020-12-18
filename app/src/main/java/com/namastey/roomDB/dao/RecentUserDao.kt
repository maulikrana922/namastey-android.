package com.namastey.roomDB.dao

import androidx.room.*
import com.namastey.roomDB.entity.RecentUser

@Dao
interface RecentUserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addRecentUser(recentUser: RecentUser)

    @Delete
    fun deleteRecentUser(recentUser: RecentUser)

    @Query("SELECT * FROM recentuser")
    fun getRecentUser(): RecentUser

    @Update
    fun updateRecentUser(recentUser: RecentUser)

    @Query(value = "SELECT * FROM recentuser  LIMIT 0,5")
    fun getAllRecentUser(): List<RecentUser>
}