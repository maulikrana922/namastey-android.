package com.namastey.uiView

import com.namastey.model.CategoryBean

interface DashboardView: BaseView {

    fun onSuccessCategory(categoryBeanList: ArrayList<CategoryBean>)

}