package com.namastey.uiView

import com.namastey.model.CategoryBean
import com.namastey.model.CommentBean
import com.namastey.model.DashboardBean

interface DashboardView : BaseView {

    fun onSuccessCategory(categoryBeanList: ArrayList<CategoryBean>)
    fun onSuccessFeed(dashboardList: ArrayList<DashboardBean>)
    fun onSuccessAddComment(commentBean: CommentBean)
    fun onSuccessGetComment(data: ArrayList<CommentBean>)
    fun onSuccessProfileLike(data: Any)
    fun onSuccessFollow(msg: String)
    fun onSuccessReport(msg: String)
    fun onSuccessBlockUser(msg: String)
    fun onSuccessSavePost(msg: String)
}