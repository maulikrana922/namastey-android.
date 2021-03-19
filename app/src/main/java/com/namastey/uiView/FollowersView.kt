package com.namastey.uiView

import com.namastey.model.DashboardBean
import java.util.*

interface FollowersView : BaseView {
    fun onSuccess(list: ArrayList<DashboardBean>)

}