package com.namastey.roomDB.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecentLocations(
    @PrimaryKey
    var id: Int,
    var city: String,
    var state: String,
    var country: String,
    var postalCode: String,
    var knownName: String,
    var currentTime: Long,
    var latitude: Double,
    var longitude: Double

) {
    constructor() : this(
        0,
        "",
        "",
        "",
        "",
        "",
        0,
        0.0,
        0.0
    )
}