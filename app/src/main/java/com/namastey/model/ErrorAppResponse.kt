package com.namastey.model

import com.google.gson.annotations.SerializedName

class ErrorAppResponse<T> {

    @SerializedName("status")
    var status: Int = 0
    var message: String = ""
    var errors: T? = null
}
