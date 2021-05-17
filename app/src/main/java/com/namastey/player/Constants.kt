package com.namastey.player

import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.namastey.model.DashboardBean

/**
 * Create By Dinesh songra
 */
interface Constants {
    companion object {
        const val VIDEO_LIST: String = "VIDEO_LIST"
        var simpleCache: SimpleCache? = null
        val videoList: ArrayList<DashboardBean> = ArrayList<DashboardBean>();

    }

}