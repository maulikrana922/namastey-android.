package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class TrandingBean() : Parcelable {

    var id: Int = 0
    var name: String = ""
    var image: String = ""

    constructor(parcel: Parcel) : this() {
        parcel.readInt()
        parcel.readString() ?: ""
        parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TrandingBean> {
        override fun createFromParcel(parcel: Parcel): TrandingBean {
            return TrandingBean(parcel)
        }

        override fun newArray(size: Int): Array<TrandingBean?> {
            return arrayOfNulls(size)
        }
    }
}