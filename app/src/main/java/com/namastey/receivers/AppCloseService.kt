package com.namastey.receivers

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import com.namastey.model.AppTimeResponse
import com.namastey.networking.ApiClient
import com.namastey.networking.ApiInterface
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import okhttp3.*
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
        //startApiCall()
        println("onTaskRemoved called")
        Log.e("AppCloseService", "onTaskRemoved called")
        //Utils.stopAppCountTimer(this)
        super.onTaskRemoved(rootIntent)
        //this.stopSelf()
    }

    private fun startApiCall() {
        val SPEND_APP_TIME = SessionManager(this).getStringValue(Constants.KEY_SPEND_APP_TIME)
        try {
            Utils.stopAppCountTimer(this@AppCloseService)
        } catch (e: Exception) {
            e.message
        }
        Log.e("time", SPEND_APP_TIME)
/*
        val client = OkHttpClient()
        val urlBuilder =
            HttpUrl.parse(Constants.BASE.plus("add-user-active-time"))!!.newBuilder()
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
                */
/*val data: String = response.body()!!.string()
                Log.e("AppCloseService", "data.: \t $data")*//*

            }
        })
*/

        val token = "Bearer " + SessionManager(this).getAccessToken()
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val call: retrofit2.Call<AppTimeResponse> = apiInterface.appTime(token, SPEND_APP_TIME)
        call.enqueue(object : retrofit2.Callback<AppTimeResponse> {
            override fun onResponse(
                call: retrofit2.Call<AppTimeResponse>,
                response: retrofit2.Response<AppTimeResponse>
            ) {

                try {
                    Log.e(
                        "AppCloseService", Gson().toJson(response.body()!!.message)
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: retrofit2.Call<AppTimeResponse>, t: Throwable) {
                Log.e("AppCloseService", "" + t.message)
            }
        })
    }

    override fun onDestroy() {
        startApiCall()
        println("onDestroy called")
        Log.e("AppCloseService", "onDestroy called")
        super.onDestroy()
    }
}
