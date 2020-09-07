package com.namastey.uiView

import com.namastey.model.DashboardBean

interface FolloFollowersView : BaseView {
    fun onSuccessSearchList(userList: ArrayList<DashboardBean>)

}