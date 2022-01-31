package com.namastey.uiView

import com.namastey.model.CategoryBean
import com.namastey.model.InterestBean
import com.namastey.model.InterestSubCategoryBean
import com.namastey.roomDB.entity.User
import java.util.*

interface ChooseInterestView : BaseView {

    fun onClose()

    fun onNext()

    fun onSuccess(interestList: ArrayList<InterestBean>)

    fun onSuccessAllCategoryList(interestList: ArrayList<InterestSubCategoryBean>)

    fun onSuccessCreateOrUpdate(user: User)

    fun onSuccessCategory(categoryBeanList: ArrayList<CategoryBean>)

}