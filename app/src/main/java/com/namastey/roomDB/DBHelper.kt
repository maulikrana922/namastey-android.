package com.namastey.roomDB

import com.namastey.roomDB.entity.RecentLocations
import com.namastey.roomDB.entity.RecentUser
import com.namastey.roomDB.entity.User
import com.namastey.scopes.PerApplication
import java.util.*
import javax.inject.Inject

@PerApplication
class DBHelper @Inject constructor(var appDB: AppDB) {

    //For User Table
    fun setLoggedInUser(user: User): List<Long> {
        return appDB.userDao().setLoggedInUser(user)
    }

    fun updateUser(user: User) {
        return appDB.userDao().updateUser(user)
    }

    fun getUser(): User {
        return appDB.userDao().getUser()
    }


    // For Recent User Table
    fun addRecentUser(recentUser: RecentUser) {
        return appDB.recentUserDao().addRecentUser(recentUser)
    }

    fun getAllRecentUser(): List<RecentUser> {
        return appDB.recentUserDao().getAllRecentUser()
    }

    fun getExistingRecentUser(userId: Int): Boolean {
        return appDB.recentUserDao().getExistingRecentUser(userId)
    }

    fun deleteExistingUser(userId: Int) {
        return appDB.recentUserDao().deleteExistingUser(userId)
    }

    // For Recent Location Table
    fun addRecentLocation(recentLocations: RecentLocations) {
        return appDB.recentLocationDao().addRecentLocations(recentLocations)
    }

    fun updateRecentLocations(recentLocations: RecentLocations) {
        return appDB.recentLocationDao().updateRecentLocations(recentLocations)
    }

    fun updateAllRecentLocations(recentLocationList: ArrayList<RecentLocations>) {
        return appDB.recentLocationDao().updateAllRecentLocations(recentLocationList)
    }

    fun addAllRecentLocation(recentLocationList: ArrayList<RecentLocations>) {
        return appDB.recentLocationDao().addAllRecentLocations(recentLocationList)
    }

    fun getAllRecentLocations(): List<RecentLocations> {
        return appDB.recentLocationDao().getAllRecentLocations()
    }

    fun getLastRecentLocations(): RecentLocations {
        return appDB.recentLocationDao().getLastRecentLocations()
    }

    fun getExistingRecentLocation(locationId: Int): Boolean {
        return appDB.recentLocationDao().getExistingRecentLocations(locationId)
    }

    fun deleteExistingLocation(locationId: Int) {
        return appDB.recentLocationDao().deleteExistingLocations(locationId)
    }

    fun updateSelectedLocation(isSelected: Boolean, locationId: Int) {
        return appDB.recentLocationDao().updateSelectedLocation(isSelected, locationId)
    }

}