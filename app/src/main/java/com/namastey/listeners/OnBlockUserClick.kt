package com.namastey.listeners

interface OnBlockUserClick {

    fun onUnblockUserClick(userId: Long, position: Int)
    fun onUserItemClick(userId: Long)
}