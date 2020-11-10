package com.namastey.uiView

import com.namastey.model.ActivityListBean
import com.namastey.model.FollowRequestBean

interface NotificationView: BaseView {
    fun onSuccessFollowRequest(data: ArrayList<FollowRequestBean>)
    fun onSuccessActivityList(activityList: ArrayList<ActivityListBean>)

}