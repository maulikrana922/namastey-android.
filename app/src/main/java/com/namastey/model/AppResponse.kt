package com.namastey.model

import com.google.gson.annotations.SerializedName

class AppResponse<T> {

    /*We can also defined response param with @SerializedName("paramname")*/
    @SerializedName("status")
    var status: Boolean = false

    var error: Int = 0

    var message: String = ""

    var data: T? = null
}
