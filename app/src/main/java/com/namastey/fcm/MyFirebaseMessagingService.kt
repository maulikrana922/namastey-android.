package com.namastey.fcm

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.namastey.R
import com.namastey.activity.DashboardActivity
import com.namastey.activity.MatchesActivity
import com.namastey.utils.Constants
import com.namastey.utils.Constants.ACTION_ACTION_TYPE
import com.namastey.utils.SessionManager
import org.json.JSONException
import org.json.JSONObject
import com.namastey.model.Notification as NotificationModel


class MyFirebaseMessagingService : FirebaseMessagingService() {

    //private val TAG = "FirebaseMessagingService"

    private val TAG = MyFirebaseMessagingService::class.java.simpleName


    private var notificationCount = 0
    private var getNotification = NotificationModel()


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //SessionManager(applicationContext).setFirebaseToken(token)
        println("$TAG token --> $token")

        SessionManager(applicationContext).setFirebaseToken(token)
    }

    /*override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            if (remoteMessage.notification != null) {
                showNotification(
                    remoteMessage.notification?.title,
                    remoteMessage.notification?.body
                )
            } else {
                showNotification(remoteMessage.data["title"], remoteMessage.data["message"])
            }

        } catch (e: Exception) {
            println("$tag error -->${e.localizedMessage}")
        }
    }*/

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e("MessageService", "remoteMessage: " + remoteMessage.notification)
        if (remoteMessage.notification != null) {
            Log.e(TAG, "Data: " + remoteMessage.data)
            Log.e(TAG, "body: " + remoteMessage.notification!!.body)
            try {
                // val mainJsonObject = JSONObject(remoteMessage.data as Map<*, *>)
                val mainJsonObject = JSONObject(remoteMessage.data.toString())
                Log.e(TAG, "Payload: " + mainJsonObject.getString("payload"))

                handleDataMessage(mainJsonObject)
                /*showNotification(
                    remoteMessage.notification?.title,
                    remoteMessage.notification?.body
                )*/

                Log.e("MessageService", "isBackground: ${isBackground()}")

            } catch (e: JSONException) {
                Log.d("error", e.message!!)
            }
        }
    }

    private fun handleDataMessage(json: JSONObject) {
        Log.e(TAG, "push json: $json")
        try {
            val data = json.getJSONObject("data")
            val title = data.getString("title")
            val message = data.getString("message")
            val isBackground = data.getBoolean("is_background")
            val imageUrl = data.getString("image")
            val timestamp = data.getString("timestamp")
            val payload = data.getJSONObject("payload")
            Log.e(TAG, "title: $title")
            Log.e(TAG, "message: $message")
            Log.e(TAG, "isBackground: $isBackground")
            Log.e(TAG, "payload: $payload")
            Log.e(TAG, "imageUrl: $imageUrl")
            Log.e(TAG, "timestamp: $timestamp")
            if (!isBackground) {
                sendBroadcastDashboard(title, message)
            } else {
                // app is in background, show the notification in notification tray
                showNotification(title, message)
                val resultIntent = Intent(
                    applicationContext,
                    MatchesActivity::class.java
                )
                resultIntent.putExtra("message", message)

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(
                        applicationContext,
                        title,
                        message,
                        timestamp,
                        resultIntent
                    )
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(
                        applicationContext,
                        title,
                        message,
                        timestamp,
                        resultIntent,
                        imageUrl
                    )
                }
            }
        } catch (e: JSONException) {
            Log.e(TAG, "Json Exception: " + e.message)
        } catch (e: Exception) {
            Log.e(TAG, "Exception: " + e.message)
        }
    }

    private fun showNotificationMessage(
        context: Context,
        title: String,
        message: String,
        timeStamp: String,
        intent: Intent
    ) {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        showNotification(title, message) //, timeStamp, intent)
    }


    private fun showNotificationMessageWithBigImage(
        context: Context,
        title: String,
        message: String,
        timeStamp: String,
        intent: Intent,
        imageUrl: String
    ) {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        showNotification(title, message)//, timeStamp, intent, imageUrl)
    }

    private fun sendBroadcastDashboard(notiTitle: String?, notiMessage: String?) {
        /**
         * Add data to notification Object
         * */
        val intent = Intent(NOTIFICATION_ACTION)
        intent.putExtra(KEY_NOTIFICATION_COUNT, notificationCount)
        intent.putExtra(KEY_NOTIFICATION, getNotification)
        intent.putExtra(Constants.NOTIFICATION_TYPE, Constants.NOTIFICATION_BROADCAST)
        intent.putExtra(KEY_NOTI_TITLE, notiTitle)
        intent.putExtra(KEY_NOTI_MESSAGE, notiMessage)
        sendBroadcast(intent)
    }

    private fun showNotification(title: String?, body: String?) {
        var intent = Intent()

        if (isBackground()) {
            intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra(ACTION_ACTION_TYPE, "notification")
            intent.putExtra(KEY_NOTIFICATION_COUNT, notificationCount)
            intent.putExtra(KEY_NOTIFICATION, getNotification)
            intent.putExtra(Constants.NOTIFICATION_TYPE, Constants.NOTIFICATION_PENDING_INTENT)
        } else {
            intent = Intent(this, MatchesActivity::class.java)
            intent.putExtra(ACTION_ACTION_TYPE, "notification")
            intent.putExtra(KEY_NOTIFICATION_COUNT, notificationCount)
            intent.putExtra(KEY_NOTIFICATION, getNotification)
            intent.putExtra(Constants.NOTIFICATION_TYPE, Constants.NOTIFICATION_BROADCAST)
        }

        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId = getString(R.string.channel_id)
        val channelName = getString(R.string.notifiy)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupNotificationChannels(channelId, channelName, notificationManager)
        }

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationBuilder.setDefaults(NotificationManager.IMPORTANCE_HIGH)
        } else {
            notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun isBackground(): Boolean {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = am.runningAppProcesses
        for (processInfo in runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (activeProcess in processInfo.pkgList) {
                    if (activeProcess == packageName) {
                        return false
                    }
                }
            }
        }
        return true
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupNotificationChannels(
        channelId: String,
        channelName: String,
        notificationManager: NotificationManager
    ) {

        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        channel.enableLights(true)
        channel.lightColor = Color.BLACK
        channel.enableVibration(true)
        notificationManager.createNotificationChannel(channel)
    }


    companion object {
        const val KEY_NT = "NT"
        const val KEY_NOTIFICATION = "notification"
        const val KEY_NOTI_TITLE = "noti_title"
        const val KEY_NOTI_MESSAGE = "noti_message"
        const val KEY_NOTIFICATION_COUNT = "notification_count"
        const val NOTIFICATION_ACTION = "notification-action"
    }
}
