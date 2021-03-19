package com.namastey.uiView

import com.namastey.model.SocialAccountBean
import java.util.*

interface ProfileInterestView : BaseView {

    fun onSuccessResponse(data: ArrayList<SocialAccountBean>)
    fun onSuccessSpotify(spotifyUrl: String)
}