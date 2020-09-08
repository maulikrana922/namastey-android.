package com.namastey.uiView

import com.namastey.model.DashboardBean

interface FindFriendView: BaseView {
    fun onSuccessSuggestedList(suggestedList: ArrayList<DashboardBean>)
    fun onSuccessSearchList(suggestedList: ArrayList<DashboardBean>)
}