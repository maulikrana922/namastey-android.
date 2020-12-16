package com.namastey.roomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.namastey.roomDB.dao.RecentUserDao
import com.namastey.roomDB.dao.UserDao
import com.namastey.roomDB.entity.RecentUser
import com.namastey.roomDB.entity.User

@Database(entities = [User::class, RecentUser::class], version = 1, exportSchema = false)
abstract class AppDB : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recentUserDao(): RecentUserDao

    companion object {
        var INSTANCE: AppDB? = null

        fun getAppDataBase(context: Context): AppDB? {
            if (INSTANCE == null) {
                synchronized(AppDB::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, AppDB::class.java, "namastey").allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase() {
            INSTANCE = null
        }
    }
}
