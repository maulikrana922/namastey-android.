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
    fun inject(activity: DashboardActivity)
    fun inject(fragment: CountryFragment)
    fun inject(fragment: SelectFilterFragment)
    fun inject(activity: FilterActivity)
    fun inject(activity: EditActivity)
    fun inject(activity: EditInterestActivity)
    fun inject(activity: MembershipActivity)
    fun inject(activity: PostVideoActivity)
    fun inject(activity: AlbumDetailActivity)
    fun inject(activity: ProfileViewActivity)
    fun inject(activity: AlbumVideoActivity)
    fun inject(activity: MatchesActivity)
    fun inject(fragment: NotificationFragment)
    fun inject(activity: MatchesScreenActivity)
    fun inject(fragment: FollowRequestFragment)
    fun inject(activity: SettingsActivity)
    fun inject(activity: AccountSettingsActivity)
    fun inject(fragment: AccountSettingsFragment)
    fun inject(fragment: AccountSettingsNotificationFragment)
    fun inject(fragment: BlockListFragment)
    fun inject(fragment: ManageAccountFragment)
    fun inject(fragment: ContentLanguagesFragment)
    fun inject(fragment: SafetyFragment)
    fun inject(fragment: SafetySubFragment)
    fun inject(fragment: ChatSettingsFragment)
    fun inject(activity: ChatActivity)
    fun inject(fragment: PersonalizeDataFragment)
    fun inject(activity: LocationActivity)
    fun inject(activity: LikeProfileActivity)
    fun inject(fragment: LikeSentFragment)
    fun inject(fragment: LikedUserPostFragment)
    fun inject(activity: ImageSliderActivity)
    fun inject(activity: SearchLocationActivity)
    fun inject(activity: InAppPurchaseActivity)
    fun inject(activity: InviteActivity)
    fun inject(activity: OTPActivity)
    fun inject(activity: LanguageActivity)
    fun inject(activity: InterestActivity)
    fun inject(activity: EducationActivity)
    fun inject(activity: NameActivity)
    fun inject(activity: AddVideoActivity)
    fun inject(activity: FollowingActivity)
    fun inject(activity: FollowersActivity)
    fun inject(activity: NotificationActivity)
    fun inject(activity: FollowRequestActivity)
    fun inject(activity: CommentActivity)
    fun inject(activity: SocialLinkActivity)
    fun inject(activity: MemberActivity)

    //    fun inject(activity: EmailActivity)
    fun inject(activity: ProfilePicActivity)
}
