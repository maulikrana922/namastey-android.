package com.namastey.uiView

import com.namastey.model.DashboardBean
import com.namastey.roomDB.entity.User

interface ProfileView: BaseView {

    fun onSuccessResponse(dashboardBean: DashboardBean)
    fun onSuccessProfileResponse(user: User)
}