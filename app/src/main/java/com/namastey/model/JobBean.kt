package com.namastey.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


class JobBean() : Parcelable {

    var id: Long = 0
    var title: String = ""
    var company_name: String = ""
    @SerializedName("is_selected")
    var isSelect: Int  = 0

    constructor(parcel: Parcel) : this() {
        parcel.readLong()
        parcel.readString() ?: ""
        parcel.readString()
        isSelect = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(company_name)
        parcel.writeInt(isSelect)
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