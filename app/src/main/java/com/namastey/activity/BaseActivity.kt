package com.namastey.activity

import android.app.Activity
import androidx.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import com.namastey.R
import com.namastey.application.NamasteyApplication
import com.namastey.dagger.component.ActivityComponent
import com.namastey.dagger.module.ViewModule
import com.namastey.listeners.OnInteractionWithFragment
import com.namastey.uiView.BaseView
import com.namastey.utils.Constants
import com.namastey.utils.Constants.INVALID_SESSION_ERROR_CODE
import com.namastey.utils.CustomAlertDialog
import retrofit2.HttpException

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity(), BaseView {

    private lateinit var viewDataBinding: T
    private lateinit var onInteractionWithFragment: OnInteractionWithFragment

    protected fun getActivityComponent(): ActivityComponent {
        return NamasteyApplication.appComponent()
            .activityComponent(ViewModule(this))
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

    override fun onFailed(msg: String, error: Int) {

        when (error) {
            INVALID_SESSION_ERROR_CODE -> {
                // logout from app and go to launch screen
            }
            else -> {
                showMsg(msg)
            }
        }
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

    fun addFragmentChild(childFragmentManager: FragmentManager,fragment: Fragment, tag: String) {
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

    fun openActivity(
        activity: Activity,
        destinationActivity: Activity
    ) {
        startActivity(Intent(activity, destinationActivity::class.java))
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }
    fun openActivity(
        intent: Intent
    ) {
        startActivity(intent)
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    fun openActivityForResult(
        intent: Intent,
        resultCode: Int
    ) {
        startActivityForResult(intent,resultCode)
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    fun finishActivity(){
        finish()
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left)
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

}