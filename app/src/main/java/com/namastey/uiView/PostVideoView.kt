package com.namastey.uiView

import com.namastey.model.VideoBean

interface PostVideoView : BaseView {
    fun onSuccessPostVideoDesc(videoBean: VideoBean)

    fun onSuccessPostVideo(videoBean: VideoBean)
    fun onSuccessPostCoverImage(videoBean: VideoBean)

}