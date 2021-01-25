package com.namastey.receivers

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import okhttp3.*
import java.io.IOException
import java.util.*

class AppCloseService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        Log.e("AppCloseService", "onBind called")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("AppCloseService", "onCreate called")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.e("AppCloseService", "onStartCommand called")
        println("onStartCommand called")
        Utils.startAppCountTimer(this)
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        println("onTaskRemoved called")
        Log.e("AppCloseService", "onTaskRemoved called")
        startApiCall()
        Utils.stopAppCountTimer(this)
        super.onTaskRemoved(rootIntent)
        //this.stopSelf()
    }

    private fun startApiCall() {
        val SPEND_APP_TIME = SessionManager(this).getStringValue(Constants.KEY_SPEND_APP_TIME)

        val client = OkHttpClient()
        val urlBuilder =
            HttpUrl.parse("https://testyourapp.online/namasteyapp/api/add-user-active-time")!!.newBuilder()
        urlBuilder.addQueryParameter("total_time", SPEND_APP_TIME)
        val url = urlBuilder.build().toString()
        Log.e("AppCloseService", "url: $url")

        val request: Request = Request.Builder()
            .header(Constants.API_KEY, "Bearer " + SessionManager(this).getAccessToken())
            .header("Content-Type", "application/json")
            .removeHeader("Pragma")
            .header(
                "Cache-Control",
                String.format(Locale.getDefault(), "max-age=%d", Constants.CACHE_TIME)
            )
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException) {
                e.printStackTrace()
                Log.e("AppCloseService", "Request Failed.")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call?, response: Response) {
                if (!response.isSuccessful) {
                    throw IOException("Error : $response")
                } else {
                    Log.e("AppCloseService", "Request Successful.")
                    Log.e("AppCloseService", "Response: \t ${response.message()}")
                    Log.e("AppCloseService", "Response: \t ${response.isSuccessful}")
                    Log.e("AppCloseService", "Response: \t $response")
                }

                // Read data in the worker thread
                /*val data: String = response.body()!!.string()
                Log.e("AppCloseService", "data.: \t $data")*/
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        println("onDestroy called")
        Log.e("AppCloseService", "onDestroy called")

    }
}
