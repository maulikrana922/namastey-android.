package com.namastey.uiView

import com.namastey.model.InstagramData
import com.namastey.model.SocialAccountBean
import java.util.*

interface ProfileInterestView : BaseView {

    fun onSuccessResponse(data: ArrayList<SocialAccountBean>)
    fun onSuccessAddLinkResponse(data: ArrayList<SocialAccountBean>)
    fun onSuccessSpotify(spotifyUrl: String,name:String)
    fun onSuccessInstagram(instagramData: InstagramData)
}