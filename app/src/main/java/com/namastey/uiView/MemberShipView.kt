package com.namastey.uiView

import com.namastey.model.MembershipPriceBean
import java.util.*

interface MemberShipView: BaseView {
    fun onSuccessMembershipList(membershipView: ArrayList<MembershipPriceBean>)
}