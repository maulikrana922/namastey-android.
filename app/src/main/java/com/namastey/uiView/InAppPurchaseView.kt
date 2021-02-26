package com.namastey.uiView

import com.namastey.model.PurchaseBean

interface InAppPurchaseView : BaseView {
    fun onSuccess(purchaseBean: PurchaseBean)
    fun onSuccessPurchaseStatus(purchaseBean: PurchaseBean)
}