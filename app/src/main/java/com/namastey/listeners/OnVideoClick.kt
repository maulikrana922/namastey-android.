package com.namastey.listeners

import com.namastey.model.VideoBean

interface OnVideoClick {

    fun onUpnextClick(position: Int)
    fun onVideoClick()
    fun onCommentClick(postId: Long)
    fun onShareClick(videoBean: VideoBean)
    fun onPostViewer(postId: Long)
    fun onPostEdit(position: Int,videoBean: VideoBean)
    fun onClickLike(position: Int, videoBean: VideoBean)
}