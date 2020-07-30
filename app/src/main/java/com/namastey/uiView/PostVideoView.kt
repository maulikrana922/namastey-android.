package com.namastey.uiView

import com.namastey.model.AlbumBean
import com.namastey.model.VideoBean
import java.util.ArrayList

interface PostVideoView : BaseView {
    fun onSuccessPostVideoDesc(videoBean: VideoBean)

    fun onSuccessPostVideo(videoBean: VideoBean)
    fun onSuccessPostCoverImage(videoBean: VideoBean)
    fun onSuccessAlbumList(arrayList: ArrayList<AlbumBean>)

}