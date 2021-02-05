package com.namastey.uiView

import com.namastey.model.BoostPriceBean
import com.namastey.model.DashboardBean
import com.namastey.model.ProfileBean

interface ProfileView: BaseView {

    fun onSuccessResponse(profileBean: ProfileBean)
    fun onSuccessFollow(profileBean: ProfileBean)
    fun onSuccessProfileLike(dashboardBean: DashboardBean)
    fun onSuccessReport(msg: String)
    fun onSuccessBlockUser(msg: String)
    fun onSuccessSavePost(msg: String)
    fun onSuccessBoostPriceList(boostPriceBean: ArrayList<BoostPriceBean>)
    fun onLogoutSuccess(msg: String)
    fun onLogoutFailed(msg: String, error: Int)

//    fun onSuccessProfileResponse(user: User)
}