package com.namastey.uiView

import com.namastey.model.DashboardBean
import com.namastey.model.ProfileBean

interface ProfileView: BaseView {

    fun onSuccessResponse(profileBean: ProfileBean)
    fun onSuccessFollow(profileBean: ProfileBean)
    fun onSuccessProfileLike(dashboardBean: DashboardBean)

//    fun onSuccessProfileResponse(user: User)
}