package com.namastey.uiView

import com.namastey.model.InterestBean
import com.namastey.roomDB.entity.User

interface ChooseInterestView: BaseView{

    fun onClose()

    fun onNext()

    fun onSuccess(interestList: ArrayList<InterestBean>)

    fun onSuccessCreateOrUpdate(user: User)
}