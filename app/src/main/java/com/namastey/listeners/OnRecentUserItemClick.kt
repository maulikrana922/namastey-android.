package com.namastey.listeners

import com.namastey.roomDB.entity.RecentUser

interface OnRecentUserItemClick {

    fun onItemRecentUserClick(recentUser: RecentUser)
}