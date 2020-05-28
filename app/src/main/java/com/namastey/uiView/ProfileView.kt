package com.namastey.uiView

import com.namastey.model.DashboardBean

interface ProfileView: BaseView {

    fun onSuccessResponse(dashboardBean: DashboardBean)
}