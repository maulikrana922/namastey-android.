package com.namastey.uiView

import com.namastey.model.CategoryBean
import java.util.*

interface ProfileSelectCategoryView: BaseView {

    fun onSuccessCategory(categoryBeanList: ArrayList<CategoryBean>)

}