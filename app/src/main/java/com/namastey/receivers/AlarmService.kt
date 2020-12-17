package com.namastey.receivers

import android.app.IntentService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.namastey.R
import com.namastey.activity.ProfileActivity

class AlarmService : IntentService("AlarmService") {
    private var alarmNotificationManager: NotificationManager? = null
    override fun onHandleIntent(intent: Intent?) {
        sendNotification("Wake Up! Wake Up!")
    }

    private fun sendNotification(msg: String) {
        Log.e("AlarmService", "Preparing to send notification...: $msg")
        alarmNotificationManager = this
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        val contentIntent: PendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, ProfileActivity::class.java), 0
        )
        val alamNotificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            this
        ).setContentTitle("Alarm").setSmallIcon(R.mipmap.ic_launcher)
           // .setStyle(Notification.BigTextStyle().bigText(msg))
            .setContentText(msg)
        alamNotificationBuilder.setContentIntent(contentIntent)
        alarmNotificationManager!!.notify(1, alamNotificationBuilder.build())
        Log.e("AlarmService", "Notification sent.")
    }
}