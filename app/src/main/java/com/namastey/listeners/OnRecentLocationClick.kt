package com.namastey.listeners

import com.namastey.roomDB.entity.RecentLocations
import java.util.*

interface OnRecentLocationClick {

    fun onRecentLocationItemClick(recentLocation: RecentLocations, recentLocationList: ArrayList<RecentLocations>)
}