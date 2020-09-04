package com.namastey.uiView

import com.namastey.model.DashboardBean

interface FollowersView : BaseView {
    fun onSuccess(list: ArrayList<DashboardBean>)

}