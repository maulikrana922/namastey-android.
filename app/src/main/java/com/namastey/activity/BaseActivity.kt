package com.namastey.activity

import android.arch.lifecycle.Observer
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.namastey.R
import com.namastey.application.NamasteyApplication
import com.namastey.dagger.component.ActivityComponent
import com.namastey.dagger.module.ViewModule
import com.namastey.uiView.BaseView
import com.namastey.utils.Constants.INVALID_SESSION_ERROR_CODE
import retrofit2.HttpException

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity(), BaseView {

    private lateinit var viewDataBinding: T

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
        getViewModel().showSnackBar(getString(msgId))
    }

    override fun showMsg(msg: String) {
        getViewModel().showSnackBar(msg)
    }

    override fun hideKeyboard() {
        val inputMethodManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
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
}