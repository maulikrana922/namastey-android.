package com.namastey.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


class EducationBean() : Parcelable {

    var id: Long = 0
    var college: String = ""
    var year: String = ""
    var title: String = ""
    var course: String = ""
    @SerializedName("is_selected")
    var isSelect: Int  = 0

    constructor(parcel: Parcel) : this() {
        parcel.readLong()
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        isSelect = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(college)
        parcel.writeString(year)
        parcel.writeString(title)
        parcel.writeString(course)
        parcel.writeInt(isSelect)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EducationBean> {
        override fun createFromParcel(parcel: Parcel): EducationBean {
            return EducationBean(parcel)
        }

        override fun newArray(size: Int): Array<EducationBean?> {
            return arrayOfNulls(size)
        }
    }
}