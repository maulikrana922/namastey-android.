package com.namastey.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.namastey.utils.Constants.EMAIL
import com.namastey.utils.Constants.KEY_INTERNET_AVAILABLE
import com.namastey.utils.Constants.KEY_LOGIN_TYPE
import com.namastey.utils.Constants.KEY_SESSION_TOKEN
import com.namastey.utils.Constants.KEY_USER_ID
import com.namastey.utils.Constants.KEY_USER_NAME
import com.namastey.utils.Constants.MOBILE

class SessionManager(context: Context) {

    private var mPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun logout(userId: Long) {
        mPrefs.edit().clear().apply()
        setUserId(userId)
    }

    fun logout() {
        mPrefs.edit().clear().apply()
    }

    fun getUserId(): Long {
        return mPrefs.getLong(KEY_USER_ID, 0)
    }

    fun setUserId(userId: Long) {
        val e = mPrefs.edit()
        e.putLong(KEY_USER_ID, userId)
        e.apply()
    }

    fun getAccessToken(): String {
        return mPrefs.getString(KEY_SESSION_TOKEN, "")!!
    }

    fun setAccessToken(firebaseToken: String) {
        val e = mPrefs.edit()
        e.putString(KEY_SESSION_TOKEN, firebaseToken)
        e.apply()
    }

    fun getUserPhone(): String {
        return mPrefs.getString(MOBILE, "")!!
    }

    fun setUserPhone(phone: String) {
        val e = mPrefs.edit()
        e.putString(MOBILE, phone)
        e.apply()
    }

    fun getUserEmail(): String {
        return mPrefs.getString(EMAIL, "")!!
    }

    fun setUserEmail(email: String) {
        val e = mPrefs.edit()
        e.putString(EMAIL, email)
        e.apply()
    }

    fun getLoginType(): String {
        return mPrefs.getString(KEY_LOGIN_TYPE, "")!!
    }

    fun setLoginType(loginType: String) {
        val e = mPrefs.edit()
        e.putString(KEY_LOGIN_TYPE, loginType)
        e.apply()
    }

    fun setUserName(merchantName: String) {
        val e = mPrefs.edit()
        e.putString(KEY_USER_NAME, merchantName)
        e.apply()
    }

    fun getUserName(): String {
        return mPrefs.getString(KEY_USER_NAME, "")!!
    }
    fun setInternetAvailable(isInternetAvail: Boolean) {
        val e = mPrefs.edit()
        e.putBoolean(KEY_INTERNET_AVAILABLE, isInternetAvail)
        e.apply()
        Log.w("InternetMonitor ", "onAvailable " + mPrefs.getBoolean(KEY_INTERNET_AVAILABLE, false))
    }
}