package com.namastey.dagger.component

import com.namastey.activity.*
import com.namastey.dagger.module.ActivityModule
import com.namastey.dagger.module.ViewModule
import com.namastey.fragment.*
import com.namastey.scopes.PerActivity
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = [ViewModule::class, ActivityModule::class])
interface ActivityComponent {
    fun inject(activity: SplashActivity)
    fun inject(activity: SignUpActivity)
    fun inject(activity: DemoActivity)
    fun inject(activity: Demo1Activity)
    fun inject(fragment: SignupWithPhoneFragment)
    fun inject(fragment: OTPFragment)
    fun inject(fragment: SelectGenderFragment)
    fun inject(fragment: VideoLanguageFragment)
    fun inject(fragment: ChooseInterestFragment)
    fun inject(activity: DashboardActivity)
    fun inject(activity: ProfileActivity)
}
