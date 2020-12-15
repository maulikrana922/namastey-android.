package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class MembershipPriceBean() : Parcelable {

    var membership_type: Int = 0
    var price: String = ""
    var discount_pr: String = ""

    constructor(parcel: Parcel) : this() {
        membership_type = parcel.readInt()
        price = parcel.readString() ?: ""
        discount_pr = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(membership_type)
        parcel.writeString(price)
        parcel.writeString(discount_pr)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MembershipPriceBean> {
        override fun createFromParcel(parcel: Parcel): MembershipPriceBean {
            return MembershipPriceBean(parcel)
        }

        override fun newArray(size: Int): Array<MembershipPriceBean?> {
            return arrayOfNulls(size)
        }
    }
}