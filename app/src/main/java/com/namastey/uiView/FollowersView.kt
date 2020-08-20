package com.namastey.uiView

import com.namastey.model.ProfileBean

interface FollowersView : BaseView {
    fun onSuccess(list: ArrayList<ProfileBean>)

}