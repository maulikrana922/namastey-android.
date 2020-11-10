package com.namastey.uiView

import com.namastey.model.SafetyBean

interface SafetyView : BaseView {
    fun onSuccessResponse(safetyBean: SafetyBean)

}