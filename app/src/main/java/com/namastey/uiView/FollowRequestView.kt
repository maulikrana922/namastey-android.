package com.namastey.uiView

import com.namastey.model.FollowRequestBean

interface FollowRequestView: BaseView {
    fun onSuccessFollowRequest(data: ArrayList<FollowRequestBean>)

}