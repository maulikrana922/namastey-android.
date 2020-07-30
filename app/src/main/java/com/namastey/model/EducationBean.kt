package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class EducationBean() : Parcelable {

    var user_education_Id: Long = 0
    var collegeName: String = ""
    var year: String = ""
    var title: String = ""
    var course: String = ""

    constructor(parcel: Parcel) : this() {
        parcel.readLong()
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(user_education_Id)
        parcel.writeString(collegeName)
        parcel.writeString(year)
        parcel.writeString(title)
        parcel.writeString(course)
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