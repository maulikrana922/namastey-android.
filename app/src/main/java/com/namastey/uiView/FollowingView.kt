package com.namastey.uiView

import com.namastey.model.DashboardBean
import java.util.*

interface FollowingView : BaseView {

    fun onSuccess(list: ArrayList<DashboardBean>)
    fun onSuccessStartChat(msg: String)
}