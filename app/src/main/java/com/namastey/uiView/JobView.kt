package com.namastey.uiView

import com.namastey.model.JobBean

interface JobView: BaseView {
    fun onSuccessResponse(jobBean: JobBean)
    fun onSuccessJobList(jobList: ArrayList<JobBean>)

}