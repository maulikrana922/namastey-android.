package com.namastey.dagger.component

import android.content.Context
import com.namastey.application.NamasteyApplication
import com.namastey.dagger.module.AppDBModule
import com.namastey.dagger.module.NetworkModule
import com.namastey.dagger.module.ViewModule
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.scopes.PerApplication
import dagger.Component
import dagger.android.AndroidInjectionModule

@PerApplication
@Component(modules = [AndroidInjectionModule::class, NetworkModule::class, AppDBModule::class])
interface AppComponent {
    fun inject(namasteyApplication: NamasteyApplication)
    fun activityComponent(viewModule: ViewModule): ActivityComponent
    fun dbHelper(): DBHelper
    fun networkService(): NetworkService
    fun context(): Context

}
