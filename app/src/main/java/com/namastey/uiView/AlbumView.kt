package com.namastey.uiView

import com.namastey.model.AlbumBean
import com.namastey.model.CommentBean
import java.util.ArrayList

interface AlbumView: BaseView {

    fun onSuccessAlbumList(arrayList: ArrayList<AlbumBean>)
    fun onSuccessAddComment(commentBean: CommentBean)
    fun onSuccessGetComment(data: ArrayList<CommentBean>)
}