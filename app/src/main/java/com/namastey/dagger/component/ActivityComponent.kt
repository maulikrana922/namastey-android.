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
    fun inject(fragment: SignupWithPhoneFragment)
    fun inject(fragment: OTPFragment)
    fun inject(fragment: SelectGenderFragment)
    fun inject(fragment: VideoLanguageFragment)
    fun inject(fragment: ChooseInterestFragment)
    fun inject(activity: DashboardActivity)
    fun inject(activity: ProfileActivity)
    fun inject(fragment: CountryFragment)
    fun inject(activity: FollowingFollowersActivity)
    fun inject(fragment: FollowersFragment)
    fun inject(fragment: FollowingFragment)
    fun inject(fragment: FindFriendFragment)
    fun inject(fragment: SelectFilterFragment)
    fun inject(activity: FilterActivity)
    fun inject(fragment: AddFriendFragment)
    fun inject(fragment: SignUpFragment)
    fun inject(activity: ProfileBasicInfoActivity)
    fun inject(activity: ProfileInterestActivity)
    fun inject(fragment: SelectCategoryFragment)
    fun inject(fragment: InterestInFragment)
    fun inject(fragment: EducationFragment)
    fun inject(fragment: JobFragment)
    fun inject(fragment: AddLinksFragment)
    fun inject(activity: CreateAlbumActivity)
    fun inject(activity: EditProfileActivity)
    fun inject(fragment: EditProfileFragment)
    fun inject(fragment: AlbumFragment)
    fun inject(activity: PostVideoActivity)
    fun inject(activity: EducationListActivity)
    fun inject(activity: JobListingActivity)
    fun inject(activity: AlbumDetailActivity)
    fun inject(activity: ProfileViewActivity)
}
