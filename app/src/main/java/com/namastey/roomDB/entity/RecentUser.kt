package com.namastey.roomDB.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecentUser(
    @PrimaryKey
    var id: Long,
    var user_id: Long,
    var album_id: Long,
    var viewers: Long,
    var description: String,
    var username: String,
    var profile_url: String,
    var cover_image_url: String,
    var video_url: String,
    var job: String,
    var is_comment: Int,
    var share_with: Int,
    var is_download: Int,
    var comments: Int,
    var is_follow: Int,
    var is_like: Int,
    var isChecked: Int,
    var is_match: Int,
    var user_profile_type: Int,
    var is_liked_you: Int,
    var current_time: Long

    ) {
    constructor() : this(
        0,
        0,
        0,
        0,
        "",
        "",
        "",
        "",
        "",
        "",
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0
    )
}