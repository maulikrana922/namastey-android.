package com.namastey.listeners

import com.namastey.roomDB.entity.RecentLocations

interface OnRecentLocationClick {

    fun onRecentLocationItemClick(recentLocation: RecentLocations)
}