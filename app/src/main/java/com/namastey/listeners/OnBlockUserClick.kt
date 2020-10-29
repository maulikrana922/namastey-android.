package com.namastey.listeners

interface OnBlockUserClick {

    fun onUnblockUserClick(userId: Long, idBlock: Int, position: Int)
    fun onUserItemClick(userId: Long)
}