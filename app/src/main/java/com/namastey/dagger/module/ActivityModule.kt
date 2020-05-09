package com.namastey.dagger.module

import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.scopes.PerActivity
import com.namastey.uiView.BaseView
import dagger.Module
import dagger.Provides

@Module
class ActivityModule {

    @Provides
    @PerActivity
    fun getViewModelFactory(
        networkService: NetworkService,
        dbHelper: DBHelper,
        baseView: BaseView
    ) = ViewModelFactory(networkService, dbHelper, baseView)

}