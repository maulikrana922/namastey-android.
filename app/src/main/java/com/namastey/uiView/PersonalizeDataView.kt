package com.namastey.uiView

import com.namastey.model.SafetyBean

interface PersonalizeDataView : BaseView {
    fun onSuccessResponse(safetyBean: SafetyBean)

}