package com.namastey.uiView

import com.namastey.model.AlbumBean
import com.namastey.model.CommentBean
import com.namastey.model.DashboardBean
import java.util.*

interface AlbumView: BaseView {

    fun onSuccessAlbumList(arrayList: ArrayList<AlbumBean>)
    fun onSuccessAddComment(commentBean: CommentBean)
    fun onSuccessGetComment(data: ArrayList<CommentBean>)
    fun onSuccessSavePost(msg: String)
    fun onSuccessReport(msg: String)
    fun onSuccessBlockUser(msg: String)
    fun onSuccessProfileLike(dashboardBean: DashboardBean)
    fun onFailedMaxLike(msg: String, error: Int)
    fun onSuccessPostShare(msg: String)
    fun onSuccessDeletePost()
    fun onSuccess(list: ArrayList<DashboardBean>)
    fun onSuccessStartChat(msg: String)
}