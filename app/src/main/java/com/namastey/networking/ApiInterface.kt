package com.namastey.networking

import com.namastey.model.AppTimeResponse
import com.namastey.utils.Constants
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {
    @FormUrlEncoded
    @POST(Constants.ADD_USER_ACTIVE_TIME)
    fun appTime(
        @Header("Authorization") user_token: String,
        @Field("total_time") total_time: String
    ): Call<AppTimeResponse>
}