package com.namastey.uiView

import com.namastey.model.CategoryBean

interface ProfileSelectCategoryView: BaseView {

    fun onSuccessCategory(categoryBeanList: ArrayList<CategoryBean>)

}