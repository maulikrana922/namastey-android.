package com.namastey.uiView

import com.namastey.model.DashboardBean
import com.namastey.model.VideoBean
import java.util.ArrayList

interface FilterView: BaseView {
    fun onSuccessSearchList(arrayList: ArrayList<DashboardBean>)
    fun onSuccessTreding(data: ArrayList<VideoBean>)

}