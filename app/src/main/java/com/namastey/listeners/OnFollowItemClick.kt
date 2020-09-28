package com.namastey.listeners

interface OnFollowItemClick {

    fun onItemRemoveFollowersClick(userId: Long, isFollow: Int, position: Int)
    fun onUserItemClick(userId: Long)
}