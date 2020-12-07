package com.namastey.uiView

interface ManageAccountView : BaseView {
    fun onLogoutSuccess(msg: String)
    fun onLogoutFailed(msg: String, error: Int)
}