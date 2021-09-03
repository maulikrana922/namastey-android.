package com.namastey.uiView

import com.namastey.model.*
import java.util.*

interface DashboardView : BaseView {

    fun onSuccessCategory(categoryBeanList: ArrayList<CategoryBean>)
    fun onSuccessFeed(dashboardList: ArrayList<DashboardBean>)
    fun onSuccessFeedFinal(dashboardList: ArrayList<DashboardBean>, total: Int)
    fun onSuccessAddComment(commentBean: CommentBean)
    fun onSuccessGetComment(data: ArrayList<CommentBean>)
    fun onSuccessProfileLike(dashboardBean: DashboardBean)
    fun onSuccessFollow(msg: String)
    fun onSuccessReport(msg: String)
    fun onSuccessBlockUser(msg: String)
    fun onSuccessSavePost(msg: String)
    fun onSuccessMention(mentionList: ArrayList<MentionListBean>)
    fun onFailedMaxLike(msg: String, error: Int)
    fun onSuccessPostShare(msg: String)
    fun onSuccessMembershipList(membershipView:  ArrayList<MembershipPriceBean>)
    fun onSuccessPurchaseStatus(purchaseBean: PurchaseBean)
    fun onSuccessBoostUse(boostBean: BoostBean)
    fun onSuccessStartChat(msg: String)
    fun onSuccess(list: ArrayList<DashboardBean>)
    fun onSuccessGlobal(msg: String)
}