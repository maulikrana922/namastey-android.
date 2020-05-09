package com.namastey.utils

import android.content.Context
import android.content.SharedPreferences

class AppPreference {
    companion object {

        private var INSTANCE: SharedPreferences? = null
        private val APP_PREFERENCE = "APP_PREFERENCE"

        const val USER_ID = "USER_ID"
        const val TOKEN = "LOGIN_TOKEN"

        fun getInstance(context: Context): SharedPreferences? {

            if (INSTANCE == null) {
                synchronized(SharedPreferences::class.java) {
                    INSTANCE = context.getSharedPreferences(
                        APP_PREFERENCE, Context.MODE_PRIVATE)
                }
            }
            return INSTANCE
        }

        fun getCheckMode(context: Context,key:String) : Boolean
        {
            return getInstance(context)!!.getBoolean(key, false)
        }

        fun getString(context: Context, key: String): String? {
            return getInstance(context)!!.getString(key, "")
        }

        fun getInt(context: Context, key: String): Int? {
            return getInstance(context)!!.getInt(key, 0)
        }

        fun save(context: Context, key: String, value: String) {
            getInstance(context)!!.edit().putString(key, value).apply()
        }

        fun save(context: Context, key: String, value: Int) {
            getInstance(context)!!.edit().putInt(key, value).apply()
        }

        fun clear(context: Context) {
            getInstance(context)!!.edit().clear().apply()
        }

    }
}
