package com.namastey.networking


import com.google.gson.JsonObject
import com.namastey.model.*
import com.namastey.roomDB.entity.Country
import com.namastey.roomDB.entity.User
import com.namastey.utils.Constants
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

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

    @GET(Constants.GET_COUNTRY)
    fun requestToGetCountryAsync(): Deferred<AppResponse<ArrayList<Country>>>

    @FormUrlEncoded
    @POST(Constants.REGISTER)
    fun sendOTPAsync(@Field(Constants.MOBILE) phone: String, @Field(Constants.EMAIL) email: String): Deferred<AppResponse<User>>

    @FormUrlEncoded
    @POST(Constants.VERIFY_OTP)
    fun verifyOTPAsync(
        @Field(Constants.MOBILE) phone: String, @Field(Constants.EMAIL) email: String, @Field(
            Constants.OTP
        ) otp: String
    ): Deferred<AppResponse<User>>

    @FormUrlEncoded
    @POST(Constants.GET_VIDEO_LANGUAGE)
    fun requestToGetVideoLanguageAsync(@Field(Constants.COUNTRY_CODE) local: String): Deferred<AppResponse<ArrayList<VideoLanguageBean>>>

    @GET(Constants.GET_INTEREST_LIST)
    fun requestToGetChooseInterestAsync(): Deferred<AppResponse<ArrayList<InterestBean>>>

    @FormUrlEncoded
    @POST(Constants.SOCIAL_LOGIN)
    fun requestSocialLoginAsync(
        @Field(Constants.EMAIL) email: String,
        @Field(Constants.USERNAME) firstName: String,
        @Field(Constants.PROVIDER) provider: String,
        @Field(Constants.PROVIDER_ID) provider_id: String,
        @Field(Constants.DEVICE_TYPE) deviceType: String = Constants.ANDROID
    ): Deferred<AppResponse<User>>

    @POST(Constants.REGISTER_GUEST)
    fun requestCreateOrUpdateUserAsync(
        @Body jsonObject: JsonObject
    ): Deferred<AppResponse<User>>

    @GET(Constants.GET_USER_DETAIL)
    fun requestToGetUserDetailAsync(): Deferred<AppResponse<DashboardBean>>

    @GET(Constants.GET_CATEGORY_LIST)
    fun requestToGetCategoryListAsync(): Deferred<AppResponse<ArrayList<CategoryBean>>>

    @Multipart
    @POST(Constants.UPDATE_PROFILE_PIC)
    fun requestUpdateProfilePicAsync(
        @Part file: MultipartBody.Part,
        @Part(Constants.DEVICE_TYPE) deviceType: RequestBody
    ): Deferred<AppResponse<User>>

    @FormUrlEncoded
    @POST(Constants.ADD_EDUCATION)
    fun addEducationAsync(
        @Field(Constants.COLLEGE) college: String, @Field(Constants.PASSING_YEAR) year: String
    ): Deferred<AppResponse<EducationBean>>

    @FormUrlEncoded
    @PUT(Constants.UPDATE_EDUCATION)
    fun updateEducationAsync(
        @Field(Constants.ID) id: String, @Field(Constants.COLLEGE) college: String, @Field(Constants.PASSING_YEAR) year: String
    ): Deferred<AppResponse<EducationBean>>
}
