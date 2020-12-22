package com.namastey.uiView

import com.namastey.model.CategoryBean
import com.namastey.model.CommentBean
import com.namastey.model.DashboardBean
import com.namastey.model.MentionListBean

interface DashboardView : BaseView {

    fun onSuccessCategory(categoryBeanList: ArrayList<CategoryBean>)
    fun onSuccessFeed(dashboardList: ArrayList<DashboardBean>)
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

}