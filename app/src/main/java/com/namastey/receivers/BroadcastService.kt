package com.namastey.receivers

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import java.util.concurrent.TimeUnit


class BroadcastService : Service() {
    //var broadIntent: Intent = Intent(COUNTDOWN_BR)
    var broadIntent: Intent = Intent("countDown")
    var countDown: CountDownTimer? = null

    override fun onCreate() {
        super.onCreate()
        countDown = object : CountDownTimer(1800000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val timer = String.format(
                    "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(
                            millisUntilFinished
                        )
                    ), // The change is in this line
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(
                            millisUntilFinished
                        )
                    )
                )
                //Log.e(TAG, "timer: $timer")
                //broadIntent.putExtra("countstart", millisUntilFinished)
                /* broadIntent.putExtra("timerCount", timer)
                 sendBroadcast(broadIntent)*/

                val intent = Intent("countDown")
                val bundle = Bundle()
                //bundle.putString("timerCount", timer)
                bundle.putLong("timerCount", millisUntilFinished)
                intent.putExtras(bundle)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }

            override fun onFinish() {
                Log.e(TAG, "Timer finished")
                SessionManager(applicationContext).setBooleanValue(false, Constants.KEY_BOOST_ME)
                cancel()
            }
        }
        (countDown as CountDownTimer).start()
    }

    override fun onDestroy() {
        //countDown!!.cancel()
        Log.e(TAG, "Timer cancelled")
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "Start Command")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val TAG = "BroadcastService"
    }
}