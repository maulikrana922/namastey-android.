package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class BoostPriceBean() : Parcelable {

    var number_of_boost: Int = 0
    var price: String = ""


    constructor(parcel: Parcel) : this() {
        number_of_boost = parcel.readInt()
        price = parcel.readString() ?: ""

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(number_of_boost)
        parcel.writeString(price)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BoostPriceBean> {
        override fun createFromParcel(parcel: Parcel): BoostPriceBean {
            return BoostPriceBean(parcel)
        }

        override fun newArray(size: Int): Array<BoostPriceBean?> {
            return arrayOfNulls(size)
        }
    }
}