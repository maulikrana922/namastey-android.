package com.namastey.listeners

import com.namastey.model.EducationBean

interface OnEducationItemClick {

    fun onEducationItemClick(educationBean: EducationBean, position: Int)

}