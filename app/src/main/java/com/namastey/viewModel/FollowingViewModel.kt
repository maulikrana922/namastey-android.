package com.namastey.viewModel

import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.FolloFollowersView
import com.namastey.uiView.FollowersView
import com.namastey.uiView.FollowingView

class FollowingViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var followingView: FollowingView = baseView as FollowingView
}