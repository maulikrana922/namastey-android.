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

    @GET(Constants.GET_COUNTRY)
    fun requestToGetCountryAsync(): Deferred<AppResponse<ArrayList<Country>>>

    @POST(Constants.REGISTER)
    fun sendOTPAsync(@Body jsonObject: JsonObject): Deferred<AppResponse<User>>

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

    @POST(Constants.SOCIAL_LOGIN)
    fun requestSocialLoginAsync(
        @Body jsonObject: JsonObject
    ): Deferred<AppResponse<User>>

    @POST(Constants.REGISTER_GUEST)
    fun requestCreateOrUpdateUserAsync(
        @Body jsonObject: JsonObject
    ): Deferred<AppResponse<User>>

    @GET(Constants.GET_USER_DETAIL)
    fun requestToGetUserDetailAsync(): Deferred<AppResponse<ProfileBean>>

    @GET(Constants.GET_USER_FULL_PROFILE)
    fun requestToGetUserFullProfileAsync(): Deferred<AppResponse<ProfileBean>>

    @GET(Constants.GET_CATEGORY_LIST)
    fun requestToGetCategoryListAsync(): Deferred<AppResponse<ArrayList<CategoryBean>>>

    @Multipart
    @POST(Constants.UPDATE_PROFILE_PIC)
    fun requestUpdateProfilePicAsync(
        @Part file: MultipartBody.Part,
        @Part(Constants.DEVICE_TYPE) deviceType: RequestBody
    ): Deferred<AppResponse<User>>

    @GET(Constants.GET_PROFILE_PIC)
    fun requestToGetProfilePicAsync(): Deferred<AppResponse<Any>>

    @GET(Constants.GET_EDUCATION)
    fun requestToGetEducationAsync(): Deferred<AppResponse<ArrayList<EducationBean>>>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = Constants.REMOVE_EDUCATION, hasBody = true)
    fun requestToRemoveEducationAsync(@Field(Constants.ID) id: Long): Deferred<AppResponse<Any>>

    @FormUrlEncoded
    @POST(Constants.ADD_EDUCATION)
    fun addEducationAsync(
        @Field(Constants.COLLEGE) college: String, @Field(Constants.COURSE) course: String
    ): Deferred<AppResponse<EducationBean>>

    @FormUrlEncoded
    @PUT(Constants.UPDATE_EDUCATION)
    fun updateEducationAsync(
        @Field(Constants.ID) id: String,
        @Field(Constants.COLLEGE) college: String,
        @Field(Constants.COURSE) course: String
    ): Deferred<AppResponse<EducationBean>>

    @GET(Constants.GET_JOB)
    fun requestToGetJobAsync(): Deferred<AppResponse<ArrayList<JobBean>>>

    @POST(Constants.ADD_UPDATE_JOB)
    fun addUpdateJobAsync(@Body jsonObject: JsonObject): Deferred<AppResponse<JobBean>>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = Constants.REMOVE_JOB, hasBody = true)
    fun requestToRemoveJobAsync(@Field(Constants.ID) id: Long): Deferred<AppResponse<Any>>

    @POST(Constants.ADD_SOCIAL_LINK)
    fun addSocialLinksAsync(@Body jsonObject: JsonObject): Deferred<AppResponse<ArrayList<SocialAccountBean>>>

    @GET(Constants.GET_SOCIAL_LINK)
    fun requestToGetSocialLinksAsync(): Deferred<AppResponse<ArrayList<SocialAccountBean>>>

    @POST(Constants.ADD_ALBUM)
    fun addAlbumAsync(@Body jsonObject: JsonObject): Deferred<AppResponse<AlbumBean>>

    @GET(Constants.GET_ALBUM)
    fun requestToGetAlbumListAsync(): Deferred<AppResponse<ArrayList<AlbumBean>>>

    @FormUrlEncoded
    @POST(Constants.GET_ALBUM_DETAILS)
    fun requestToGetAlbumDetailsAsync(@Field(Constants.ALBUM_ID) album_id: Long): Deferred<AppResponse<ArrayList<AlbumBean>>>

    @GET(Constants.GET_ALBUM_WITH_VIDEO)
    fun requestToGetAlbumWithDetailsAsync(): Deferred<AppResponse<ArrayList<AlbumBean>>>

    @FormUrlEncoded
    @POST(Constants.POST_VIDEO)
    fun postVideoAsync(
        @Field(Constants.DESCRIPTION) description: String,
        @Field(Constants.ALBUM_ID) album_id: Long,
        @Field(
            Constants.SHARE_WITH
        ) share_with: Int,
        @Field(Constants.IS_COMMENT) is_comment: Int
    ): Deferred<AppResponse<VideoBean>>

    @Multipart
    @POST(Constants.ADD_MEDIA)
    fun requestToAddMediaAsync(
        @Part post_video: MultipartBody.Part,
        @Part(Constants.FILE_TYPE) fileType: RequestBody,
        @Part(Constants.POST_VIDEO_ID) postVideoId: RequestBody,
        @Part(Constants.DEVICE_TYPE) deviceType: RequestBody
    ): Deferred<AppResponse<VideoBean>>

    @PUT(Constants.CREATE_PROFILE)
    fun requestCreateProfileAsync(@Body jsonObject: JsonObject): Deferred<AppResponse<Any>>

    @HTTP(method = "DELETE", path = Constants.POST_DELETE, hasBody = true)
    fun requestToDeletePostAsync(@Body jsonObject: JsonObject): Deferred<AppResponse<Any>>

}
