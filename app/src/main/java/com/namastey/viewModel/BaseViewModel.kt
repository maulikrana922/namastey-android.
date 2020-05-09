package com.namastey.viewModel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import java.lang.ref.WeakReference

open class BaseViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    private val baseView: BaseView
) : ViewModel() {

    private val mIsLoading = ObservableBoolean(false)
    private lateinit var viewInterface: WeakReference<BaseView>

    fun getIsLoading(): ObservableBoolean {
        return mIsLoading
    }

    fun setIsLoading(isLoading: Boolean) {
        mIsLoading.set(isLoading)
    }

    fun getViewInterface(): BaseView? {
        return baseView
    }

    fun setViewInterface(viewInterface: BaseView) {
        this.viewInterface = WeakReference(viewInterface)
    }

    fun networkService() = networkService

    fun dbHelper() = dbHelper

    private val mutableSnackBar = MutableLiveData<String>()

    fun showSnackBar(msg: String) {
        mutableSnackBar.postValue(msg)
    }

    fun afterSnackBarShow() {
        mutableSnackBar.value = null
    }

    fun getMutableSnackBar() = mutableSnackBar
}