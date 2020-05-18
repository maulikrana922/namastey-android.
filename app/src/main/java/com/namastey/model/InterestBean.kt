package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class InterestBean() : Parcelable {

    var id: Int = 0
    var interest_name: String = ""
    var image: String = ""

    constructor(parcel: Parcel) : this() {
        parcel.readInt()
        parcel.readString() ?: ""
        parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(interest_name)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InterestBean> {
        override fun createFromParcel(parcel: Parcel): InterestBean {
            return InterestBean(parcel)
        }

        override fun newArray(size: Int): Array<InterestBean?> {
            return arrayOfNulls(size)
        }
    }
}