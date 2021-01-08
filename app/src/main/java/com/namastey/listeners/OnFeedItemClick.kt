package com.namastey.listeners

import com.namastey.model.DashboardBean

interface OnFeedItemClick {

    fun onItemClick(position: Int, dashboardBean: DashboardBean)
    fun onCommentClick(position: Int, postId: Long)
    fun onProfileLikeClick(position: Int, dashboardBean: DashboardBean, isLike: Int)
    fun onUserProfileClick(dashboardBean: DashboardBean)
    fun onClickFollow(position: Int, dashboardBean: DashboardBean, isFollow: Int)
    fun onPostViewer(postId: Long)
    fun onFeedBoost(userId: Long)
    fun onDescriptionClick(userName: String)
   // fun onScrollItem(position: Int, dashboardBean: DashboardBean, playerView: PlayerView)
}