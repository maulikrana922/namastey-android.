package com.namastey.listeners

interface OnNotificationClick {

    fun onNotificationClick(userId: Long, position: Int)
    fun onClickFollowRequest(userId: Long, isFollow: Int)

}