package com.namastey.uiView

import com.namastey.model.EducationBean
import java.util.*

interface EducationView: BaseView {
    fun onSuccessResponse(educationBean: EducationBean)
    fun onSuccessEducationList(educationList: ArrayList<EducationBean>)

}