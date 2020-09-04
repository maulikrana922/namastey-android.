package com.namastey.listeners

import com.namastey.model.DashboardBean

interface OnFeedItemClick {

    fun onItemClick(dashboardBean: DashboardBean)

    fun onCommentClick(postId: Long)
    fun onProfileLikeClick(position: Int,likedUserId: Long, isLike: Int)
    fun onUserProfileClick(userId: Long)
    fun onClickFollow(position: Int, userId: Long, isFollow: Int)
}