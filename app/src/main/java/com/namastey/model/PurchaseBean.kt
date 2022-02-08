package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class PurchaseBean() : Parcelable {

    var is_purchase: Int = 0
    var number_of_boost_available: Int = 0
    var purchase_date: Long = 0
    var acknowledgementState: Int = 0
    var autoRenewing: Boolean = false
    var is_active_boost:Int=0
    var active_time:Int=0
    //var autoResumeTimeMillis: Long = 0
    var cancelReason: Int = 0
    var countryCode: String = ""
    var developerPayload:String = ""
   // var emailAddress: String = ""
    var expiryTimeMillis:String = ""
   // var externalAccountId: Int = 0
   // var familyName: String = ""
   // var givenName:String = ""
    var kind: String = ""
    var linkedPurchaseToken: String = ""
   // var obfuscatedExternalAccountId: Int = 0
   // var obfuscatedExternalProfileId: Int = 0
    var orderId: String = ""
    var paymentState: Int = 0
    var priceAmountMicros: String = ""
    var priceCurrencyCode: String = ""
   // var profileId: Int = 0
  //  var profileName: String = ""
  //  var promotionCode: Int = 0
  //  var promotionType: Int = 0
    var purchaseType: Int = 0
    var startTimeMillis: String = ""
    var invite_count: Int = 0
  //  var userCancellationTimeMillis: Int = 0

    constructor(parcel: Parcel) : this() {
        is_purchase = parcel.readInt()
        number_of_boost_available = parcel.readInt()
        purchase_date = parcel.readLong()
        acknowledgementState = parcel.readInt()
        is_active_boost = parcel.readInt()
        active_time = parcel.readInt()
        cancelReason = parcel.readInt()
        countryCode = parcel.readString()?: ""
        developerPayload = parcel.readString()?: ""
        expiryTimeMillis = parcel.readString()?: ""
        kind = parcel.readString()?: ""
        linkedPurchaseToken = parcel.readString()?: ""
        orderId = parcel.readString()?: ""
        paymentState = parcel.readInt()
        priceAmountMicros = parcel.readString()?: ""
        priceCurrencyCode = parcel.readString()?: ""
        purchaseType = parcel.readInt()
        startTimeMillis = parcel.readString()?: ""
        invite_count = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(is_purchase)
        parcel.writeInt(number_of_boost_available)
        parcel.writeLong(purchase_date)
        parcel.writeInt(acknowledgementState)
        parcel.writeInt(is_active_boost)
        parcel.writeInt(active_time)
        parcel.writeInt(cancelReason)
        parcel.writeString(countryCode)
        parcel.writeString(developerPayload)
        parcel.writeString(expiryTimeMillis)
        parcel.writeString(kind)
        parcel.writeString(linkedPurchaseToken)
        parcel.writeString(orderId)
        parcel.writeInt(paymentState)
        parcel.writeString(priceAmountMicros)
        parcel.writeString(priceCurrencyCode)
        parcel.writeInt(purchaseType)
        parcel.writeString(startTimeMillis)
        parcel.writeInt(invite_count)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PurchaseBean> {
        override fun createFromParcel(parcel: Parcel): PurchaseBean {
            return PurchaseBean(parcel)
        }

        override fun newArray(size: Int): Array<PurchaseBean?> {
            return arrayOfNulls(size)
        }
    }
}