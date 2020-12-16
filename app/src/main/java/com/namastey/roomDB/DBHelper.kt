package com.namastey.roomDB

import com.namastey.roomDB.entity.RecentUser
import com.namastey.roomDB.entity.User
import com.namastey.scopes.PerApplication
import javax.inject.Inject

@PerApplication
class DBHelper @Inject constructor(var appDB: AppDB) {

    fun setLoggedInUser(user: User): List<Long> {
        return appDB.userDao().setLoggedInUser(user)
    }

    fun updateUser(user: User) {
        return appDB.userDao().updateUser(user)
    }

    fun getUser(): User {
        return appDB.userDao().getUser()
    }

    fun addRecentUser(recentUser: RecentUser) {
        return appDB.recentUserDao().addRecentUser(recentUser)
    }

    fun deleteRecentUser(recentUser: RecentUser) {
        return appDB.recentUserDao().deleteRecentUser(recentUser)
    }

    fun getRecentUser(): RecentUser {
        return appDB.recentUserDao().getRecentUser()
    }

    fun updateRecentUser(recentUser: RecentUser) {
        return appDB.recentUserDao().updateRecentUser(recentUser)
    }

    fun getAllRecentUser(): List<RecentUser> {
        return appDB.recentUserDao().getAllRecentUser()
    }


}