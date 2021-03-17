package com.namastey.uiView

import com.namastey.model.ErrorBean
import com.namastey.model.ProfileBean
import com.namastey.model.SocialAccountBean
import java.util.*

interface ProfileBasicView : BaseView {

    fun onSuccessProfileDetails(profileBean: ProfileBean)
    fun onSuccessSocialAccount(data: ArrayList<SocialAccountBean>)
    fun onSuccessUniqueName(msg: String)
    fun onFailedUniqueName(error: ErrorBean?)
}