package com.namastey.uiView

import com.namastey.model.SocialAccountBean

interface ProfileInterestView : BaseView {

    fun onSuccessResponse(data: ArrayList<SocialAccountBean>)
//    fun onSuccessGetSocialLinks(data: ArrayList<SocialAccountBean>)
}