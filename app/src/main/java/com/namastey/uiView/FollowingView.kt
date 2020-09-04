package com.namastey.uiView

import com.namastey.model.DashboardBean

interface FollowingView : BaseView {

    fun onSuccess(list: ArrayList<DashboardBean>)
}