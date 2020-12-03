package com.namastey.listeners

interface OnNotificationClick {

    fun onNotificationClick(userId: Long, position: Int)
    fun onClickFollowRequest(position: Int, userId: Long, isFollow: Int)
    fun onPostVideoClick(position: Int, postId: Long)

}