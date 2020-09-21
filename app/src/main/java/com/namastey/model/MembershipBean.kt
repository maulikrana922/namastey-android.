package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class MembershipBean() : Parcelable {

    var id: Int = 0
    var name: String = ""
    var description: String = ""

    constructor(parcel: Parcel) : this() {
        parcel.readInt()
        parcel.readString() ?: ""
        parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MembershipBean> {
        override fun createFromParcel(parcel: Parcel): MembershipBean {
            return MembershipBean(parcel)
        }

        override fun newArray(size: Int): Array<MembershipBean?> {
            return arrayOfNulls(size)
        }
    }
}