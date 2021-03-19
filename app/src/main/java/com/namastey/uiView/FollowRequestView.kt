package com.namastey.uiView

import com.namastey.model.FollowRequestBean
import java.util.*

interface FollowRequestView: BaseView {
    fun onSuccessFollowRequest(data: ArrayList<FollowRequestBean>)

}