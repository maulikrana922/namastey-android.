package com.namastey.uiView

import com.namastey.model.DashboardBean
import java.util.*

interface FolloFollowersView : BaseView {
    fun onSuccessSearchList(userList: ArrayList<DashboardBean>)

}