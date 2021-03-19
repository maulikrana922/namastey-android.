package com.namastey.uiView

import com.namastey.model.DashboardBean
import java.util.*

interface FindFriendView: BaseView {
    fun onSuccessSuggestedList(suggestedList: ArrayList<DashboardBean>)
    fun onSuccessSearchList(suggestedList: ArrayList<DashboardBean>)
    //fun onSuccessFollowMultiple(msg: String)
}