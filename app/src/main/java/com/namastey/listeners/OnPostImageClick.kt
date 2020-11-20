package com.namastey.listeners

import com.namastey.model.VideoBean

interface OnPostImageClick {

    fun onItemPostImageClick(position: Int,videoList: ArrayList<VideoBean>)

}