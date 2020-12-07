package com.namastey.listeners

interface OnSelectUserItemClick {

    fun onSelectItemClick(userId: Long, position: Int)
    fun onSelectItemClick(userId: Long, position: Int, userProfileType: String)
}