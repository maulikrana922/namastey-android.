package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class SocialAccountBean() : Parcelable {

    var id: Long = 0
    var name: String = ""
    var link: String = ""

    constructor(parcel: Parcel) : this() {
        parcel.readLong()
        parcel.readString() ?: ""
        parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(link)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SocialAccountBean> {
        override fun createFromParcel(parcel: Parcel): SocialAccountBean {
            return SocialAccountBean(parcel)
        }

        override fun newArray(size: Int): Array<SocialAccountBean?> {
            return arrayOfNulls(size)
        }
    }
}