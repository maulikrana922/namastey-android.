package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class EducationBean() : Parcelable {

    var user_education_id: Long = 0
    var collegeName: String = ""
    var year: String = ""

    constructor(parcel: Parcel) : this() {
        parcel.readLong()
        parcel.readString() ?: ""
        parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(user_education_id)
        parcel.writeString(collegeName)
        parcel.writeString(year)
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