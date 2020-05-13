package com.namastey.viewModel

import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.SignupWithPhoneView

class SignupWithPhoneModel constructor(private val networkService: NetworkService,
                                       private val dbHelper: DBHelper,
                                       baseView: BaseView
) : BaseViewModel(networkService,dbHelper,baseView) {

    private val signupWithPhoneView = baseView as SignupWithPhoneView

    fun closeSignFragment(){
      signupWithPhoneView.onCloseSignup()
    }

    fun onClickPhone(){
        signupWithPhoneView.onClickPhone()
    }
    fun onClickEmail(){
        signupWithPhoneView.onClickEmail()
    }

    fun signupWithPhone(){

    }

    fun onClickNext(){
        signupWithPhoneView.onClickNext()
    }
}