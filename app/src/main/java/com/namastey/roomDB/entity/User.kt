package com.namastey.roomDB.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    var userId: Long,
    var user_id: Long,
    var user_uniqueId: String,
    var status: String,
    var name: String,
    var email: String,
    var mobile: String,
    var token: String,
    @Ignore
    var is_verified: Int,
    @Ignore
    var is_invited: Int,
    var user_type: Int,
    var username: String,
    @Ignore
    var is_register: Int,
    @Ignore
    var is_completly_signup: Int,
    @Ignore
    var profile_pic: String,
    @ColumnInfo(name = "is_purchase") var purchase: Int = 0

    //var is_purchase: Int = 0
) {
    constructor() : this(
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
        "",
        0,
        0,
        "",
        0
    )
}