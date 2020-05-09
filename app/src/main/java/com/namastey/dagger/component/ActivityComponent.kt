package com.namastey.dagger.component

import com.namastey.activity.Demo1Activity
import com.namastey.activity.DemoActivity
import com.namastey.activity.SignUpActivity
import com.namastey.activity.SplashActivity
import com.namastey.dagger.module.ActivityModule
import com.namastey.dagger.module.ViewModule
import com.namastey.scopes.PerActivity
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = [ViewModule::class, ActivityModule::class])
interface ActivityComponent {
    fun inject(activity: SplashActivity)
    fun inject(activity: SignUpActivity)
    fun inject(activity: DemoActivity)
    fun inject(activity: Demo1Activity)
}
