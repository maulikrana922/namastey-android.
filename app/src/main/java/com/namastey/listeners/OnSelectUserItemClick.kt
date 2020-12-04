package com.namastey.listeners

interface OnSelectUserItemClick {

    fun onSelectItemClick(be: Long, position: Int)
    fun onSelectItemClick(userId: Long, position: Int, userProfileType: String)
}