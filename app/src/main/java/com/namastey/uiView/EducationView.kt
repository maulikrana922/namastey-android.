package com.namastey.uiView

import com.namastey.model.EducationBean

interface EducationView: BaseView {
    fun onSuccessResponse(educationBean: EducationBean)

}