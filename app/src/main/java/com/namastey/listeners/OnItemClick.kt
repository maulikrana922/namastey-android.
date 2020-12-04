package com.namastey.listeners

import com.namastey.model.DashboardBean

interface OnItemClick {

    fun onItemClick(value: Long, position: Int)
    fun onItemFollowingClick(dashboardBean: DashboardBean)

}