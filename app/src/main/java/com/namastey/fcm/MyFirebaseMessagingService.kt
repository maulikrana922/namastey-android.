package com.namastey.fcm

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.namastey.R
import com.namastey.activity.ChatActivity
import com.namastey.activity.DashboardActivity
import com.namastey.activity.MatchesActivity
import com.namastey.activity.ProfileActivity
import com.namastey.model.MatchesListBean
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import com.namastey.model.Notification as NotificationModel

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val tag = "FirebaseMessagingService"

    private var notificationCount = 0
    private var getNotification = NotificationModel()
    private var postImage = ""


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //SessionManager(applicationContext).setFirebaseToken(token)
        println("$tag token --> $token")

        SessionManager(applicationContext).setFirebaseToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e("MessageService", "Message data payload: " + remoteMessage.data)
        try {
            val mainJsonObject = JSONObject(remoteMessage.data as Map<*, *>)
            Log.e("MessageService", "mainJsonObject: $mainJsonObject")
            if (mainJsonObject.has("notification_count")) {
                val notificationCount = mainJsonObject.getString("notification_count")
                Log.e("MessageService", "notificationCount: $notificationCount")
                getNotification.notificationCount = notificationCount
            }
            if (mainJsonObject.has("is_notification_type")) {
                val isNotificationType = mainJsonObject.getString("is_notification_type")
                getNotification.isNotificationType = isNotificationType
            }
            if (mainJsonObject.has("NT")) {
                val nt = mainJsonObject.getString("NT")
                getNotification.nt = nt
            }
            if (mainJsonObject.has("alert")) {
                val alert = mainJsonObject.getString("alert")
                getNotification.alert = alert
            }
            if (mainJsonObject.has("title")) {
                val title = mainJsonObject.getString("title")
                getNotification.title = title
            }
            if (mainJsonObject.has("message")) {
                val message = mainJsonObject.getString("message")
                getNotification.message = message
            }
            if (mainJsonObject.has("created_at")) {
                val createdAt = mainJsonObject.getString("created_at")
                getNotification.createdAt = createdAt
            }
            if (mainJsonObject.has("post_image")) {
                //val postImage = mainJsonObject.getString("post_image")
                postImage = mainJsonObject.getString("post_image")
                getNotification.postImage = postImage
            }
            if (mainJsonObject.has("profile_pic")) {
                postImage = mainJsonObject.getString("profile_pic")
                getNotification.postImage = postImage
            }
            if (mainJsonObject.has("is_read")) {
                val isRead = mainJsonObject.getString("is_read")
                getNotification.isRead = isRead
            }

            if (SessionManager(applicationContext).getUserId() != 0L) {
                showNotification(
                    mainJsonObject,
                    getNotification.title,
                    getNotification.message,
                    postImage
                )
            }
            Log.e("MessageService", "isBackground: ${isBackground()}")

        } catch (e: JSONException) {
            Log.e("error", e.message!!)
        }
//        }
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

    private fun showNotification(
        jsonObject: JSONObject,
        title: String?,
        body: String?,
        postImage: String
    ) {
        var intent = Intent()

        var openChatActivity = false
        if (isBackground()) {
            intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra(Constants.ACTION_ACTION_TYPE, "notification")
            intent.putExtra(KEY_NOTIFICATION_COUNT, notificationCount)
            intent.putExtra(KEY_NOTIFICATION, getNotification)
            intent.putExtra(Constants.NOTIFICATION_TYPE, Constants.NOTIFICATION_PENDING_INTENT)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

            if (getNotification.isNotificationType == "7"){
//                intent =
//                    Intent(this, DashboardActivity::class.java)
                val matchesListBean = MatchesListBean()
                matchesListBean.username = jsonObject.getString("username")
                matchesListBean.profile_pic = jsonObject.getString("profile_pic")
                matchesListBean.id = jsonObject.getLong("user_id")
                matchesListBean.is_match = jsonObject.getInt("is_match")
                intent.putExtra("isFromMessage", true)
                intent.putExtra("chatNotification", true)
                intent.putExtra("matchesListBean",matchesListBean)
                Log.d("Chat notification :","6 pass")
                if (ChatActivity.isChatActivityOpen){
                    if (ChatActivity.userId == matchesListBean.id)
                        openChatActivity = true
                }
            }
        } else {
            intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra(Constants.ACTION_ACTION_TYPE, "notification")
            intent.putExtra(KEY_NOTIFICATION_COUNT, notificationCount)
            intent.putExtra(KEY_NOTIFICATION, getNotification)
            intent.putExtra(Constants.NOTIFICATION_TYPE, Constants.NOTIFICATION_BROADCAST)

            when (getNotification.isNotificationType) {
                "0", "1", "5" -> { // for Comment Notification
                    intent =
                        Intent(this, MatchesActivity::class.java)
                    intent.putExtra("onClickMatches", false)
                }
//                "1" -> { // for mention in comment Notification
//                    intent
//                        Intent(this, MatchesActivity::class.java)
//                    intent.putExtra("onClickMatches", false)
//                }
                "2" -> { // for follow Notification
                    intent =
                        Intent(this, ProfileActivity::class.java)
                    intent.putExtra("isMyProfile", true)
                }
                "3" -> { // for new video Notification
                    intent =
                        Intent(this, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                "4" -> { // for Matches Notification
                    intent =
                        Intent(this, MatchesActivity::class.java)
                    intent.putExtra("onClickMatches", true)
                }
//                "5" -> { // for mention in post Notification
//                    intent =
//                        Intent(this, MatchesActivity::class.java)
//                    intent.putExtra("onClickMatches", false)
//                }
                "6" -> { // for follow request Notification
                    intent =
                        Intent(this, MatchesActivity::class.java)
                    intent.putExtra("onFollowRequest", true)
                }
                "7" -> { // For New message
                    intent =
                        Intent(this, ChatActivity::class.java)
                    val matchesListBean = MatchesListBean()
                    matchesListBean.username = jsonObject.getString("username")
                    matchesListBean.profile_pic = jsonObject.getString("profile_pic")
                    matchesListBean.id = jsonObject.getLong("user_id")
                    matchesListBean.is_match = jsonObject.getInt("is_match")
                    intent.putExtra("isFromMessage", true)
                    intent.putExtra("chatNotification", true)
                    intent.putExtra("matchesListBean",matchesListBean)
                    Log.d("Chat notification :","6 pass")
                    if (ChatActivity.isChatActivityOpen){
                        if (ChatActivity.userId == matchesListBean.id)
                            openChatActivity = true
                    }

                }
                else -> { // Default
//                    intent =
//                        Intent(this, MatchesActivity::class.java)
//                    intent.putExtra("onClickMatches", false)
                    intent =
                        Intent(this, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
            }
        }
        //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        if (!openChatActivity) {
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            val channelId = getString(R.string.channel_id)
            val channelName = getString(R.string.notifiy)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val sound: Uri =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE.toString() + "://" + packageName + "/" + R.raw.notification_beap)
            val attributes: AudioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                    NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
                channel.enableLights(true)
                channel.lightColor = Color.BLACK
                channel.enableVibration(true)
                channel.setSound(sound,attributes)
                notificationManager.createNotificationChannel(channel)
            }

            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setLargeIcon(getBitmapFromURL(postImage))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setSmallIcon(R.drawable.ic_notification);
                notificationBuilder.color = Color.RED;
            } else {
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                notificationBuilder.setSound(sound);
            }

            notificationManager.notify(getNotification.notificationCount.toInt(), notificationBuilder.build())
        }
    }

    private fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
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
    companion object {
        const val KEY_NT = "NT"
        const val KEY_NOTIFICATION = "notification"
        const val KEY_NOTI_TITLE = "noti_title"
        const val KEY_NOTI_MESSAGE = "noti_message"
        const val KEY_NOTIFICATION_COUNT = "notification_count"
        const val NOTIFICATION_ACTION = "notification-action"
    }
}