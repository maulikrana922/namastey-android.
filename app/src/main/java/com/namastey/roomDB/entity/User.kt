package com.namastey.roomDB.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    var userId: Long,
    var status: String,
    var name: String,
    var email: String,
    var mobile: String,
    var token: String,
    @Ignore
    var is_verified: Int
) {
    constructor() : this(0,"", "", "", "","",0)
}