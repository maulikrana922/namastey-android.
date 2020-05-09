package com.namastey.dagger.module

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.viewModel.*
import javax.inject.Inject


class ViewModelFactory @Inject constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    private val baseView: BaseView
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel(networkService,dbHelper,baseView) as T
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> SignUpViewModel(networkService,dbHelper,baseView) as T
            modelClass.isAssignableFrom(DemoViewModel::class.java) -> DemoViewModel(networkService, dbHelper, baseView) as T
            modelClass.isAssignableFrom(DemoView1Model::class.java) -> DemoView1Model(networkService, dbHelper, baseView) as T
            else -> BaseViewModel(networkService, dbHelper, baseView) as T
        }

    }
}