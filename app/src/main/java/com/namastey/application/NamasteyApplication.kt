package com.namastey.application

import android.app.Activity
import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.namastey.dagger.component.AppComponent
import com.namastey.dagger.component.DaggerAppComponent
import com.namastey.dagger.module.AppDBModule
import com.namastey.dagger.module.ContextModule
import com.namastey.dagger.module.NetworkModule
import com.namastey.utils.InternetConnectionMonitor
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject


class NamasteyApplication : Application(), HasActivityInjector {

    @Inject
    internal lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    private var isUpdateProfile : Boolean = false
    override fun activityInjector(): AndroidInjector<Activity> {
        return activityDispatchingAndroidInjector
    }

    companion object {
        private lateinit var appComponent: AppComponent
        fun appComponent() = appComponent
        lateinit var instance: NamasteyApplication
    }

    override fun onCreate() {
        super.onCreate()
        AppEventsLogger.activateApp(this);
        buildAppComponent()
        instance = this
        InternetConnectionMonitor(this).enable()
    }

    private fun buildAppComponent() {
        appComponent = DaggerAppComponent.builder()
            .appDBModule(AppDBModule())
            .contextModule(ContextModule(this))
            .networkModule(NetworkModule())
            .build()
    }

    fun setIsUpdateProfile(isUpdateProfile: Boolean) {
        this.isUpdateProfile = isUpdateProfile
    }

    fun isUpdateProfile(): Boolean = isUpdateProfile
}
