package com.namastey.uiView

import com.namastey.model.JobBean
import java.util.*

interface JobView: BaseView {
    fun onSuccessResponse(jobBean: JobBean)
    fun onSuccessJobList(jobList: ArrayList<JobBean>)

}