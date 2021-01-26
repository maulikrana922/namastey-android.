package com.namastey.uiView

import com.namastey.model.ChatMessage
import java.util.*

interface ChatBasicView: BaseView {
    fun onSuccessReport(msg: String)
    fun onSuccessBlockUser(msg: String)
    fun onSuccessDeleteMatches(msg: String)
    fun onSuccessAdminMessage(data: ArrayList<ChatMessage>)
    fun onSuccessMuteNotification(message: String)

}