package com.namastey.uiView

import com.namastey.model.ProfileBean

interface FollowingView: BaseView {

    fun onSuccess(list: ArrayList<ProfileBean>)
}