package com.namastey.listeners

import com.namastey.model.VideoBean
import java.util.*

interface OnPostImageClick {

    fun onItemPostImageClick(position: Int,videoList: ArrayList<VideoBean>)

}