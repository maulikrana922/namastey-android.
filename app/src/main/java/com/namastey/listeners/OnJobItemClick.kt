package com.namastey.listeners

import com.namastey.model.JobBean

interface OnJobItemClick {

    fun onJobItemClick(jobBean: JobBean, position: Int)

}