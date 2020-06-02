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

    /*If you want to pass params as a form data request*/
    suspend fun requestLogin(email: String, password: String): AppResponse<User> =
        withContext(Dispatchers.IO) {
            networkRequest.requestLoginAsync(email, password).await()
        }

    suspend fun requestLogout(): AppResponse<Any> = withContext(Dispatchers.IO) {
        networkRequest.requestLogout().await()
    }

    suspend fun requestToGetCountry(): AppResponse<ArrayList<Country>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetCountryAsync().await()
        }

    suspend fun requestSendOTP(phone: String, email: String): AppResponse<User> =
        withContext(Dispatchers.IO) { networkRequest.sendOTPAsync(phone, email).await() }

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
        email: String,
        username: String,
        provider: String,
        providerId: String
    ): AppResponse<User> = withContext(Dispatchers.IO) {
        networkRequest.requestSocialLoginAsync(
            email,
            username,
            provider,
            providerId
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
}
