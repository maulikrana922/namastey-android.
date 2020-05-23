package com.namastey.roomDB

import androidx.room.Database
import androidx.room.RoomDatabase
import com.namastey.roomDB.dao.UserDao
import com.namastey.roomDB.entity.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDB : RoomDatabase() {
    abstract fun userDao(): UserDao
}
