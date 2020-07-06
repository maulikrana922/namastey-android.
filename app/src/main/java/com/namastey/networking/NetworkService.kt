package com.namastey.networking

import com.google.gson.JsonObject
import com.namastey.model.*
import com.namastey.roomDB.entity.Country
import com.namastey.roomDB.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class NetworkService(private val networkRequest: NetworkRequest) {

    /*If you want to pass params as json request then use below
    suspend fun requestLogin(loginRequest: LoginRequest): AppResponse<Login> = withContext(Dispatchers.Default) {
    networkRequest.requestLogin(loginRequest).await()
    }
    */

    suspend fun requestToGetCountry(): AppResponse<ArrayList<Country>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetCountryAsync().await()
        }

    suspend fun requestSendOTP(jsonObject: JsonObject): AppResponse<User> =
        withContext(Dispatchers.IO) { networkRequest.sendOTPAsync(jsonObject).await() }

    suspend fun requestVerifyOTP(phone: String, email: String, otp: String): AppResponse<User> =
        withContext(Dispatchers.IO) { networkRequest.verifyOTPAsync(phone, email, otp).await() }

    suspend fun requestToGetVideoLanguage(locale: String): AppResponse<ArrayList<VideoLanguageBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetVideoLanguageAsync(locale).await()
        }

    suspend fun requestToGetChooseInterest(): AppResponse<ArrayList<InterestBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetChooseInterestAsync().await()
        }

    suspend fun requestSocialLogin(
        jsonObject: JsonObject
    ): AppResponse<User> = withContext(Dispatchers.IO) {
        networkRequest.requestSocialLoginAsync(
            jsonObject
        ).await()
    }

    suspend fun requestToUpdateOrCreateUser(
        jsonObject: JsonObject
    ): AppResponse<User> = withContext(Dispatchers.IO) {
        networkRequest.requestCreateOrUpdateUserAsync(
            jsonObject
        ).await()
    }

    suspend fun requestToGetUserDetail(): AppResponse<DashboardBean> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetUserDetailAsync().await()
        }

    suspend fun requestToGetCategoryList(): AppResponse<ArrayList<CategoryBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetCategoryListAsync().await()
        }

    suspend fun requestUpdateProfilePicAsync(
        profile_image: MultipartBody.Part,
        deviceType: RequestBody
    ): AppResponse<User> = withContext(Dispatchers.IO) {
        networkRequest.requestUpdateProfilePicAsync(
            profile_image,
            deviceType
        ).await()
    }

    suspend fun requestAddEducation(college: String, year: String): AppResponse<EducationBean> =
        withContext(Dispatchers.IO) { networkRequest.addEducationAsync(college, year).await() }

    suspend fun requestUpdateEducation(
        id: String,
        college: String,
        year: String
    ): AppResponse<EducationBean> =
        withContext(Dispatchers.IO) {
            networkRequest.updateEducationAsync(id, college, year).await()
        }

    suspend fun requestAddUpdateJob(jsonObject: JsonObject): AppResponse<JobBean> =
        withContext(Dispatchers.IO) { networkRequest.addUpdateJobAsync(jsonObject).await() }

    suspend fun requestAddSocialLinks(jsonObject: JsonObject): AppResponse<ArrayList<SocialAccountBean>> =
        withContext(Dispatchers.IO) { networkRequest.addSocialLinksAsync(jsonObject).await() }

    suspend fun requestToGetSocialLinksList(): AppResponse<ArrayList<SocialAccountBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetSocialLinksAsync().await()
        }

    suspend fun requestAddUpdateAlbum(jsonObject: JsonObject): AppResponse<AlbumBean> =
        withContext(Dispatchers.IO) { networkRequest.addAlbumAsync(jsonObject).await() }

    suspend fun requestToGetAlbumListList(): AppResponse<ArrayList<AlbumBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetAlbumListAsync().await()
        }

    suspend fun requestPostVideo(
        description: String,
        album_id: Long,
        share_with: Int,
        is_comment: Int
    ): AppResponse<VideoBean> =
        withContext(Dispatchers.IO) {
            networkRequest.postVideoAsync(
                description,
                album_id,
                share_with,
                is_comment
            ).await()
        }

    suspend fun requestToAddMediaAsync(
        mbVideo: MultipartBody.Part,
        fileType: RequestBody,
        postVideoId: RequestBody,
        rbDeviceType: RequestBody
    ): AppResponse<VideoBean> = withContext(Dispatchers.IO) {
        networkRequest.requestToAddMediaAsync(
           mbVideo,
            fileType,
            postVideoId,
            rbDeviceType
        ).await()
    }

}
