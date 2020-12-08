package com.namastey.uiView

interface SettingsView : BaseView {
    fun onSuccessHideProfile(message: String)
    fun onSuccessProfileType(message: String)
    fun onLogoutSuccess(msg: String)
    fun onLogoutFailed(msg: String, error: Int)
}