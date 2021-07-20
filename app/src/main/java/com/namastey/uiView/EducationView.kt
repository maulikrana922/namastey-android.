package com.namastey.uiView

import com.namastey.model.EducationBean
import com.namastey.model.JobBean
import java.util.*

interface EducationView: BaseView {
    fun onSuccessResponse(educationBean: EducationBean)
    fun onSuccessEducationList(educationList: ArrayList<EducationBean>)
    fun onSuccessResponseJob(jobBean: JobBean)

}