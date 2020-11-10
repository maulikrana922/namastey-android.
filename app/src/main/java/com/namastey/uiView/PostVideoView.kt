package com.namastey.uiView

import com.namastey.model.AlbumBean
import com.namastey.model.MentionListBean
import com.namastey.model.VideoBean
import java.util.*

interface PostVideoView : BaseView {
    fun onSuccessPostVideoDesc(videoBean: VideoBean)

    fun onSuccessPostVideo(videoBean: VideoBean)
    fun onSuccessPostCoverImage(videoBean: VideoBean)
    fun onSuccessAlbumList(arrayList: ArrayList<AlbumBean>)
    fun onSuccessMention(mentionList: ArrayList<MentionListBean>)

}