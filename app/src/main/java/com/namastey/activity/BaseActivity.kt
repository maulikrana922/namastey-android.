package com.namastey.activity

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.namastey.R
import com.namastey.application.NamasteyApplication
import com.namastey.dagger.component.ActivityComponent
import com.namastey.dagger.module.ViewModule
import com.namastey.fcm.MyFirebaseMessagingService
import com.namastey.listeners.OnInteractionWithFragment
import com.namastey.uiView.BaseView
import com.namastey.utils.Constants
import com.namastey.utils.Constants.ADMIN_BLOCK_USER_CODE
import com.namastey.utils.Constants.ADMIN_USER_UNDER_REVIEW
import com.namastey.utils.Constants.INVALID_SESSION_ERROR_CODE
import com.namastey.utils.Constants.NOTIFICATION_BROADCAST
import com.namastey.utils.CustomAlertDialog
import com.namastey.utils.CustomAlertNewDialog
import com.namastey.utils.SessionManager
import kotlinx.android.synthetic.main.dialog_delete.*
import retrofit2.HttpException

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity(), BaseView {

    private lateinit var viewDataBinding: T
    private lateinit var onInteractionWithFragment: OnInteractionWithFragment

    protected fun getActivityComponent(): ActivityComponent {
        return NamasteyApplication.appComponent().activityComponent(ViewModule(this))
    }

    protected fun bindViewData(): T {
        viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        viewDataBinding.lifecycleOwner = this
        getViewModel().setViewInterface(this)
        setUpSnackBar()
        return viewDataBinding
    }

    override fun showMsg(msgId: Int) {
        hideKeyboard()
        showAlert(getString(msgId))
    }

    override fun showMsg(msg: String) {
        hideKeyboard()
        showAlert(msg)
    }

    override fun hideKeyboard() {
        runOnUiThread {
            val inputMethodManager: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (currentFocus != null) {
                inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            }
        }
    }

    override fun onHandleException(e: Throwable) {
        var msg = ""

        if (e is java.net.UnknownHostException) {
            msg = getString(R.string.no_internet)
        } else if (e is java.net.SocketTimeoutException || e is java.net.ConnectException) {
            msg = getString(R.string.slow_internet)
        } else if (e is HttpException) {
            Log.e("BaseActivity", "Server code ->" + e.code())
            Log.e("BaseActivity", "Server response ->" + e.response().errorBody().toString())
            if (e.code() == 500) {
                msg = getString(R.string.server_error)
            }
        }

        if (!TextUtils.isEmpty(msg)) {
            runOnUiThread {
                showMsg(msg)
            }
        }
        e.printStackTrace()
    }

    override fun onSuccess(msg: String) {
        showMsg(msg)
    }

    override fun showMsgLog(msg: String) {
        Log.e("BaseActivity", "msg:  \t $msg")
    }

    override fun onFailed(msg: String, error: Int, status: Int) {
        Log.e("BaseActivity", "onFailed  error: $error")
        Log.e("BaseActivity", "onFailed  msg: $msg")
        when (status) {
            INVALID_SESSION_ERROR_CODE ->{
                runOnUiThread {
                    object : CustomAlertDialog(
                        this,
                        msg, getString(R.string.ok), ""
                    ) {
                        override fun onBtnClick(id: Int) {
                            logoutAndRedirectSignUp()
                        }
                    }.show()
                }
            }
            ADMIN_BLOCK_USER_CODE -> {
                // logout from app and go to launch screen
                logoutAndRedirectSignUp()
            }
            ADMIN_USER_UNDER_REVIEW -> {
                openActivity(Intent(this@BaseActivity, UnderReviewActivity::class.java))
            }
            else -> {
                showMsg(msg)
            }
        }
    }

    private fun logoutAndRedirectSignUp(){
        SessionManager(this@BaseActivity).logout()
        val intent = Intent(this@BaseActivity, SignUpActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        openActivity(intent)
    }

    private fun setUpSnackBar() {
        getViewModel().getMutableSnackBar().observe(this, Observer { msg ->
            msg?.let {
                Snackbar.make(currentFocus!!, msg, Snackbar.LENGTH_LONG).show()
                getViewModel().afterSnackBarShow()
            }
        })
    }

    fun addFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction().addToBackStack(tag)
            .replace(R.id.flContainer, fragment, tag)
            .commitAllowingStateLoss()
    }

   /* fun addFragmentCategory(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction().addToBackStack(tag)
            .replace(R.id.flContainerCategory, fragment, tag)
            .commitAllowingStateLoss()
    }*/

    fun addFragmentChild(childFragmentManager: FragmentManager, fragment: Fragment, tag: String) {
        childFragmentManager.beginTransaction().addToBackStack(tag)
            .add(R.id.flContainer, fragment, tag)
            .commitAllowingStateLoss()
    }

    private fun showAlert(msg: String) {
        runOnUiThread {
            object : CustomAlertDialog(
                this,
                msg, getString(R.string.ok), ""
            ) {
                override fun onBtnClick(id: Int) {
                }
            }.show()
        }
    }

    override fun isInternetAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT < 23) {
            val ni = cm.activeNetworkInfo

            if (ni != null) {
                val isInternetAvail =
                    ni.isConnected && (ni.type == ConnectivityManager.TYPE_WIFI || ni.type == ConnectivityManager.TYPE_MOBILE)
                NamasteyApplication.appComponent().sessionManager()
                    .setInternetAvailable(isInternetAvail)
                return isInternetAvail
            }
        } else {
            val n = cm.activeNetwork
            if (n != null) {
                if (Build.VERSION.SDK_INT == 28) {
                    if (isNetworkConnected()) {
                        NamasteyApplication.appComponent().sessionManager()
                            .setInternetAvailable(isNetworkConnected())
                    }
                    return isNetworkConnected()
                } else {
                    val nc = cm.getNetworkCapabilities(n)
                    var isInternetAvail = false
                    if (nc != null) {
                        isInternetAvail =
                            nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                                NetworkCapabilities.TRANSPORT_WIFI
                            )
                    }
                    NamasteyApplication.appComponent().sessionManager()
                        .setInternetAvailable(isInternetAvail)
                    return isInternetAvail
                }
            }
        }
        return false
    }

    private fun isNetworkConnected(): Boolean {
//        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
        var result = false
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }

    fun openActivity(activity: Activity, destinationActivity: Activity) {
        startActivity(Intent(activity, destinationActivity::class.java))
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    fun openActivity(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    fun openActivityForResult(intent: Intent, resultCode: Int) {
        startActivityForResult(intent, resultCode)
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    fun finishActivity() {
        finish()
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
    }

    fun setListenerOfInteractionWithFragment(onInteractionWithFragment: OnInteractionWithFragment) {
        this.onInteractionWithFragment = onInteractionWithFragment
    }

    fun getOnInteractionWithFragment() =
        if (::onInteractionWithFragment.isInitialized) onInteractionWithFragment else null

    fun isPermissionGrantedForCamera(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                return true
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    Constants.PERMISSION_CAMERA
                )
                return false
            }
        } else {
            return true
        }
    }

    fun isPermissionGrantedForContact(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                return true
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.READ_CONTACTS
                    ),
                    Constants.PERMISSION_CONTACTS
                )
                return false
            }
        } else {
            return true
        }
    }

    fun showNotification(title: String?, body: String?) {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(Constants.ACTION_ACTION_TYPE, MyFirebaseMessagingService.KEY_NOTIFICATION)
        intent.putExtra(Constants.NOTIFICATION_TYPE, NOTIFICATION_BROADCAST)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

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

    /*Change firebase token*/
//    fun getToken() {
//        val senderID = resources.getString(R.string.firebase_sender_id)
//        Thread(Runnable {
//            try {
//                val newToken = FirebaseInstanceId.getInstance().getToken(senderID, "FCM")
//                println("Token --> $newToken")
//
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }).start()
//    }

    fun completeSignUpDialog() {
        object : CustomAlertNewDialog(
            this,
            getString(R.string.msg_one_video),
            R.drawable.ic_video,
            getString(R.string.continues),
            getString(R.string.cancel)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnConfirm.id -> {
                        val intent = Intent(this@BaseActivity, ProfileViewActivity::class.java)
                        intent.putExtra("ownProfile", true)
                        openActivity(intent)
                    }
                    btnDeleteCancel.id -> {
                        dismiss()
                    }
                }
            }
        }.show()
    }

    var mServiceIntent: Intent? = null
    private var SPEND_APP_TIME = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     //   Log.e("BaseActivity", "onCreate BrandName: \t ${Build.BRAND}")

        /*mServiceIntent = Intent(this, AppCloseService()::class.java)
        if (!isMyServiceRunning(AppCloseService()::class.java)) {
            Log.e("BaseActivity", "onCreate In isMyServiceRunning")
            startService(mServiceIntent)
        }*/
        //Utils.startAppCountTimer(this)
        SPEND_APP_TIME = SessionManager(this).getStringValue(Constants.KEY_SPEND_APP_TIME)
      //  Log.e("BaseActivity", "onCreate KEY_SPEND_APP_TIME:  \t $SPEND_APP_TIME")
    }

    override fun onResume() {
        super.onResume()
        SPEND_APP_TIME = SessionManager(this).getStringValue(Constants.KEY_SPEND_APP_TIME)
       // Log.e("BaseActivity", "onResume KEY_SPEND_APP_TIME:  \t $SPEND_APP_TIME")
        if (SPEND_APP_TIME == "") {
       //     Log.e("BaseActivity", "onResume In")
        }
    }

    override fun onPause() {
        super.onPause()
        SPEND_APP_TIME = SessionManager(this).getStringValue(Constants.KEY_SPEND_APP_TIME)
       // Log.e("BaseActivity", "onPause KEY_SPEND_APP_TIME:  \t $SPEND_APP_TIME")
    }

    override fun onStop() {
        super.onStop()
        SPEND_APP_TIME = SessionManager(this).getStringValue(Constants.KEY_SPEND_APP_TIME)
       // Log.e("BaseActivity", "onStop KEY_SPEND_APP_TIME:  \t $SPEND_APP_TIME")
    }

    fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                //Log.e("BaseActivity", "Service status Running")
                //Log.e("BaseActivity", "Service name: \t ${serviceClass.name}")
               // Log.e("BaseActivity", "Service className: \t ${service.service.className}")
                return true
            }
        }
        Log.e("BaseActivity", "Service status Not running")
        return false
    }

}