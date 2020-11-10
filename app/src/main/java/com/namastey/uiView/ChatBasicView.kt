package com.namastey.uiView

interface ChatBasicView: BaseView {
    fun onSuccessReport(msg: String)
    fun onSuccessBlockUser(msg: String)
    fun onSuccessDeleteMatches(msg: String)
}