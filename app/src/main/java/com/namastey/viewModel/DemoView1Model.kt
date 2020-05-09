package com.namastey.viewModel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.roomDB.entity.User
import com.namastey.uiView.BaseView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DemoView1Model constructor(
    networkService: NetworkService,
    private val dbHelper: DBHelper,
    private val baseView: BaseView
) :
    BaseViewModel(networkService, dbHelper, baseView) {

    private lateinit var job: Job
    private val mutableUser = MutableLiveData<User>()
    val liveDataUser: LiveData<User> = mutableUser

    fun getUser() {
        job = GlobalScope.launch(Dispatchers.IO) {
            try {
                updateData(dbHelper.getUser())
            } catch (t: Throwable) {
                baseView.onHandleException(t)
            }
        }
    }

    fun onDestroy() {
        job.cancel()
    }

    private fun updateData(user: User) {
        mutableUser.postValue(user)
    }
}
