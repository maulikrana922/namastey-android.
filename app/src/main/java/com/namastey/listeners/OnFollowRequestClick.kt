package com.namastey.listeners

interface OnFollowRequestClick {

    fun onItemAllowDenyClick(userId: Long, isAllow: Int, position: Int)
    fun onFollowRequestItemClick(userId: Long, position: Int)

}