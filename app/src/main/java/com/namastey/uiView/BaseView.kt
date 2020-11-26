package com.namastey.uiView

import androidx.annotation.LayoutRes
import com.namastey.viewModel.BaseViewModel

interface BaseView {

    fun showMsg(msgId: Int)

    fun showMsg(msg: String)


    fun hideKeyboard()

    fun onHandleException(e: Throwable)

    fun onSuccess(msg: String)

    fun onFailed(msg: String, error: Int)

    fun getViewModel(): BaseViewModel

    fun isInternetAvailable(): Boolean

    @LayoutRes
    fun getLayoutId(): Int

    fun getBindingVariable(): Int
}
