package com.namastey.roomDB.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Country(
    @PrimaryKey
    var id: Int = 0,
    var phonecode: Int = 0,
    var sortname: String = "",
    var name: String = ""
)