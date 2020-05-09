package com.namastey.roomDB

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
}