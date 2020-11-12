package com.namastey.uiView

import com.namastey.model.SafetyBean

interface SafetyView : BaseView {
    fun onSuccessIsSuccessResponse(safetyBean: SafetyBean)
    fun onSuccessShareProfileSafetyResponse(safetyBean: SafetyBean)

}