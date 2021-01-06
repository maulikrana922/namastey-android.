package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class ErrorBean() : Parcelable {

    var user_name: String = ""
    var error: String = ""

    constructor(parcel: Parcel) : this() {
        user_name = parcel.readString() ?: ""
        error = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(user_name)
        parcel.writeString(error)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ErrorBean> {
        override fun createFromParcel(parcel: Parcel): ErrorBean {
            return ErrorBean(parcel)
        }

        override fun newArray(size: Int): Array<ErrorBean?> {
            return arrayOfNulls(size)
        }
    }
}