package com.namastey.uiView

import com.namastey.model.MembershipPriceBean

interface MemberShipView: BaseView {
    fun onSuccessMembershipList(membershipView:  ArrayList<MembershipPriceBean>)
}