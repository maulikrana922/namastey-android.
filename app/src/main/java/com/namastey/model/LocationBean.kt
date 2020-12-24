package com.namastey.model

class LocationBean(
    var id: Int = 0,
    var city: String = "",
    var state: String = "",
    var country: String = "",
    var postalCode: String = "",
    var knownName: String = "",
    var currentTime: Long = 0,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)