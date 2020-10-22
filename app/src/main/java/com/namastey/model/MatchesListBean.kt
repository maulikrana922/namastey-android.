package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class MatchesListBean() : Parcelable {

    var id: Long = 0
    var first_name: String = ""
    var last_name: String = ""
    var email: String = ""
    var mobile: String = ""
    var gender: String = ""
    var interest: String = ""
    var language: String = ""
    var age: String = ""
    var image: String = ""

    val viewType = 0

    constructor(parcel: Parcel) : this() {
        parcel.readLong()
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(first_name)
        parcel.writeString(last_name)
        parcel.writeString(email)
        parcel.writeString(mobile)
        parcel.writeString(gender)
        parcel.writeString(interest)
        parcel.writeString(language)
        parcel.writeString(age)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MatchesListBean> {
        const val firstImage = 1
        const val allImages = 2

        override fun createFromParcel(parcel: Parcel): MatchesListBean {
            return MatchesListBean(parcel)
        }

        override fun newArray(size: Int): Array<MatchesListBean?> {
            return arrayOfNulls(size)
        }
    }
}