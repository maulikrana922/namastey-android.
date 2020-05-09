package com.namastey.roomDB.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    var userId: Long,
    var name: String,
    var email: String,
    var phone: String
) {
    constructor() : this(0, "", "", "")
}