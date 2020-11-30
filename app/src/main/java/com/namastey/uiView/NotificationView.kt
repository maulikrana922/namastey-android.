package com.namastey.uiView

import com.namastey.model.ActivityListBean
import com.namastey.model.FollowRequestBean
import com.namastey.model.ProfileBean

interface NotificationView: BaseView {
    fun onSuccessFollowRequest(data: ArrayList<FollowRequestBean>)
    fun onSuccessActivityList(activityList: ArrayList<ActivityListBean>)
    fun onSuccessFollow(profileBean: ProfileBean)

}