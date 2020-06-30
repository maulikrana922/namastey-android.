package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class JobBean() : Parcelable {

    var id: Long = 0
    var title: String = ""
    var company_name: String = ""

    constructor(parcel: Parcel) : this() {
        parcel.readLong()
        parcel.readString() ?: ""
        parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(company_name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<JobBean> {
        override fun createFromParcel(parcel: Parcel): JobBean {
            return JobBean(parcel)
        }

        override fun newArray(size: Int): Array<JobBean?> {
            return arrayOfNulls(size)
        }
    }
}