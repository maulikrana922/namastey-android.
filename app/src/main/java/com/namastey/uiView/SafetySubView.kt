package com.namastey.uiView

import com.namastey.model.SafetyBean

interface SafetySubView : BaseView {
    fun onSuccessYourFollowerResponse(safetyBean: SafetyBean)
    fun onSuccessWhoCanCommentYourVideoResponse(safetyBean: SafetyBean)
    fun onSuccessWhoCanSendYouDirectMessageResponse(safetyBean: SafetyBean)
}