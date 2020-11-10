package com.namastey.uiView

import com.namastey.model.SafetyBean

interface SafetySubView : BaseView {
    fun onSuccessResponse(safetyBean: SafetyBean)
}