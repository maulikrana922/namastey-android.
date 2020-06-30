package com.namastey.viewModel

import android.text.TextUtils
import com.google.gson.JsonObject
import com.namastey.R
import com.namastey.model.AppResponse
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.roomDB.entity.User
import com.namastey.uiView.BaseView
import com.namastey.uiView.SignupWithPhoneView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SignupWithPhoneModel constructor(private val networkService: NetworkService,
                                       private val dbHelper: DBHelper,
                                       baseView: BaseView
) : BaseViewModel(networkService,dbHelper,baseView) {

    private val signupWithPhoneView = baseView as SignupWithPhoneView
    private lateinit var job: Job

    fun closeSignFragment(){
      signupWithPhoneView.onCloseSignup()
    }

    fun onClickPhone(){
        signupWithPhoneView.onClickPhone()
    }
    fun onClickEmail(){
        signupWithPhoneView.onClickEmail()
    }
    fun onClickCountry(){
        signupWithPhoneView.onClickCountry()
    }
    fun sendOTP(jsonObject: JsonObject) {

            setIsLoading(true)
            job = GlobalScope.launch(Dispatchers.Main){
                try {
                    if (signupWithPhoneView.isInternetAvailable()){
                        networkService.requestSendOTP(jsonObject).let { appResponse: AppResponse<User> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK){
                                signupWithPhoneView.onSuccessResponse(appResponse.data!!)
                            }else{
                                signupWithPhoneView.onFailed(appResponse.message,appResponse.error)
                            }
                        }
                    }else{
                        setIsLoading(false)
                        signupWithPhoneView.showMsg(R.string.no_internet)
                    }
                }catch (exception: Throwable){
                    setIsLoading(false)
                    signupWithPhoneView.onHandleException(exception)
                }
            }

    }

    fun onClickNext(){
        signupWithPhoneView.onClickNext()
    }

    fun isValidPhone(phone: String): Boolean {
        var msgId = 0
        when {
            TextUtils.isEmpty(phone) -> msgId = R.string.msg_empty_mobile_email
        }
        if (msgId > 0) {
            signupWithPhoneView.showMsg(msgId)
        }

        return msgId == 0
    }

    fun getCountry() {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToGetCountry().let {appResponse ->
                    signupWithPhoneView.onGetCountry(appResponse.data!!)
                }
            } catch (t: Throwable) {
                signupWithPhoneView.onHandleException(t)
            }
        }
    }

    fun onDestroy(){
        if (::job.isInitialized){
            job.cancel()
        }
    }

}