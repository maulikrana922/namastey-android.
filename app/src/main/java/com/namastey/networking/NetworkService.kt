package com.namastey.networking

import com.namastey.model.AppResponse
import com.namastey.roomDB.entity.Country
import com.namastey.roomDB.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
}
