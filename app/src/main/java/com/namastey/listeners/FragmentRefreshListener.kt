package com.namastey.listeners

import com.namastey.model.ActivityListBean

interface FragmentRefreshListener {
    fun onRefresh(activityListBean : ActivityListBean?)
}