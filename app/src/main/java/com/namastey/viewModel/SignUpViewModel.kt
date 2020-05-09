package com.namastey.viewModel

import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.SignUpView

class SignUpViewModel constructor(private val networkService: NetworkService,
                                  private val dbHelper: DBHelper,
                                  baseView: BaseView) : BaseViewModel(networkService,dbHelper,baseView) {

    private var signUpView: SignUpView = baseView as SignUpView

    fun openSignUpPhone(){
        signUpView.openSignUpWithPhone()
    }
}