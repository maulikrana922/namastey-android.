package com.namastey.listeners

import android.view.View
import com.namastey.model.DashboardBean
import me.tankery.lib.circularseekbar.CircularSeekBar

interface OnFeedItemClick {

    fun onItemClick(position: Int, dashboardBean: DashboardBean)
    fun onCommentClick(position: Int, postId: Long)
    fun onProfileLikeClick(position: Int, dashboardBean: DashboardBean, isLike: Int)
    fun onUserProfileClick(dashboardBean: DashboardBean,position: Int)
    fun onClickFollow(position: Int, dashboardBean: DashboardBean, isFollow: Int)
    fun onPostViewer(postId: Long)
    fun onFeedBoost(userId: Long)
    fun onDescriptionClick(userName: String)
    fun onBindViewItem(circularSeekBar: CircularSeekBar)
   // fun onScrollItem(position: Int, dashboardBean: DashboardBean, playerView: PlayerView)
}