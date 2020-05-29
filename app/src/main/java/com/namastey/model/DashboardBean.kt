package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class DashboardBean() : Parcelable {

    //    var id: Int = 0
    var email: String = ""
    var name: String = ""

    constructor(parcel: Parcel) : this() {
//        parcel.readInt()
        parcel.readString() ?: ""
        parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeInt(id)
        parcel.writeString(email)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DashboardBean> {
        override fun createFromParcel(parcel: Parcel): DashboardBean {
            return DashboardBean(parcel)
        }

        override fun newArray(size: Int): Array<DashboardBean?> {
            return arrayOfNulls(size)
        }
    }
}