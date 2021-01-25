package com.namastey.viewModel

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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


    private lateinit var job: Job

    fun addUserActiveTime(time: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToAddUserActiveTime(time).let { appResponse ->
                    setIsLoading(false)
                    if (appResponse.status == Constants.OK)
                        baseView.showMsgLog(appResponse.message)
                    else
                        baseView.onFailed(
                            appResponse.message,
                            appResponse.error,
                            appResponse.status
                        )
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                baseView.onHandleException(t)
            }
        }
    }
}