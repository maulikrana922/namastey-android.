package com.namastey.uiView

import com.namastey.model.ProfileBean
import com.namastey.model.SocialAccountBean

interface ProfileBasicView: BaseView {

    fun onSuccessProfileDetails(profileBean: ProfileBean)
    fun onSuccessSocialAccount(data: ArrayList<SocialAccountBean>)

}