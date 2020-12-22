package com.namastey.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager


class MaxLikeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        SessionManager(context).setBooleanValue(false, Constants.KEY_MAX_USER_LIKE)
    }
}