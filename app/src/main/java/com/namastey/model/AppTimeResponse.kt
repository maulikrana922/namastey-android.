package com.namastey.model

import com.google.gson.annotations.SerializedName

class AppTimeResponse(
    @SerializedName("status") var status: Int,
    @SerializedName("message") var message: String
)