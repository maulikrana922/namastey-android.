package com.namastey.networking


import com.namastey.model.AppResponse
import com.namastey.roomDB.entity.User
import com.namastey.utils.Constants
import kotlinx.coroutines.Deferred
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface NetworkRequest {

    /*If you want to pass params as a form data request*/
    @FormUrlEncoded
    @POST(Constants.LOGIN)
    fun requestLoginAsync(
        @Field(Constants.EMAIL) email: String, @Field(Constants.PASSWORD) password: String,
        @Field(Constants.DEVICE_TYPE) deviceType: String = "1", @Field(Constants.DEVICE_ID) deviceId: String = "0123456789", @Field(
            Constants.LATITUDE
        ) lat: String = "23.0132534", @Field(Constants.LONGITUDE) lng: String = "72.5179172"
    ): Deferred<AppResponse<User>>

    /*If you want to pass params as a json request*/
//    @POST(Constants.LOGIN)
//    fun requestLogin(@Body loginRequest: LoginRequest): Deferred<AppResponse<Login>>

    @POST(Constants.LOGOUT)
    fun requestLogout(): Deferred<AppResponse<Any>>
}
