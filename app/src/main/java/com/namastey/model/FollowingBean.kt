package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class FollowingBean() : Parcelable {

    var id: Int = 0
    var name: String = ""

    constructor(parcel: Parcel) : this() {
        parcel.readInt()
        parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FollowingBean> {
        override fun createFromParcel(parcel: Parcel): FollowingBean {
            return FollowingBean(parcel)
        }

        override fun newArray(size: Int): Array<FollowingBean?> {
            return arrayOfNulls(size)
        }
    }
}