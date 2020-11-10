package com.namastey.dagger.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> SignUpViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(SignupWithPhoneModel::class.java) -> SignupWithPhoneModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(OTPViewModel::class.java) -> OTPViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(SelectGenderViewModel::class.java) -> SelectGenderViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(VideoLanguageViewModel::class.java) -> VideoLanguageViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(ChooseInterestViewModel::class.java) -> ChooseInterestViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> DashboardViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(CountryViewModel::class.java) -> CountryViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(FollowFollowersViewModel::class.java) -> FollowFollowersViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(FollowersViewModel::class.java) -> FollowersViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(FollowingViewModel::class.java) -> FollowingViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(FindFriendViewModel::class.java) -> FindFriendViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(SelectFilterViewModel::class.java) -> SelectFilterViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(FilterViewModel::class.java) -> FilterViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(ProfileBasicViewModel::class.java) -> ProfileBasicViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(ProfileInterestViewModel::class.java) -> ProfileInterestViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(SelectCategoryViewModel::class.java) -> SelectCategoryViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(EducationViewModel::class.java) -> EducationViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(JobViewModel::class.java) -> JobViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(CreateAlbumViewModel::class.java) -> CreateAlbumViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(AlbumViewModel::class.java) -> AlbumViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(PostVideoViewModel::class.java) -> PostVideoViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(MatchesBasicViewModel::class.java) -> MatchesBasicViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(MatchesProfileViewModel::class.java) -> MatchesProfileViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(MatchesScreenViewModel::class.java) -> MatchesScreenViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(NotificationViewModel::class.java) -> NotificationViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(FollowRequestViewModel::class.java) -> FollowRequestViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(AccountSettingsViewModel::class.java) -> AccountSettingsViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(AccountSettingsNotificationViewModel::class.java) -> AccountSettingsNotificationViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(BlockListViewModel::class.java) -> BlockListViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(ManageAccountViewModel::class.java) -> ManageAccountViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(ContentLanguageViewModel::class.java) -> ContentLanguageViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(SafetyViewModel::class.java) -> SafetyViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            modelClass.isAssignableFrom(SafetySubViewModel::class.java) -> SafetySubViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
             modelClass.isAssignableFrom(ChatViewModel::class.java) -> ChatViewModel(
                networkService,
                dbHelper,
                baseView
            ) as T
            else -> BaseViewModel(networkService, dbHelper, baseView) as T

        }

    }
}