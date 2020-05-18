package com.namastey.uiView

import com.namastey.model.InterestBean

interface ChooseInterestView: BaseView{

    fun onClose()

    fun onNext()

    fun onSuccess(interestList: ArrayList<InterestBean>)
}