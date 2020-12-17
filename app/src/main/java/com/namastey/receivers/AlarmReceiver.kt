package com.namastey.receivers

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import androidx.legacy.content.WakefulBroadcastReceiver
import com.namastey.activity.ProfileActivity


class AlarmReceiver : WakefulBroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        //this will update the UI with message
        val inst: ProfileActivity = ProfileActivity()
        inst.setAlarmText("Alarm! Wake up! Wake up!")

        //this will sound the alarm tone
        //this will sound the alarm once, if you wish to
        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)
        var alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
        val ringtone: Ringtone = RingtoneManager.getRingtone(context, alarmUri)
        ringtone.play()

        //this will send a notification message
        val comp = ComponentName(
            context.packageName,
            AlarmService::class.java.name
        )
        startWakefulService(context, intent.setComponent(comp))
        resultCode = Activity.RESULT_OK
    }
}