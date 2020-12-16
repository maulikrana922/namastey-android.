package com.namastey.uiView

import com.namastey.model.*

interface NotificationView: BaseView {
    fun onSuccessFollowRequest(data: ArrayList<FollowRequestBean>)
    fun onSuccessActivityList(activityList: ArrayList<ActivityListBean>)
    fun onSuccessFollow(profileBean: ProfileBean)
    fun onSuccessPostVideoDetailResponse(videoBean: VideoBean)
    fun onSuccessMembershipList(membershipView:  ArrayList<MembershipPriceBean>)

}