package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class MatchesListBean() : Parcelable {

    var id: Long = 0
    var username: String = ""
    var email: String = ""
    var mobile: String = ""
    var gender: String = ""
    var interest: String = ""
    var language: String = ""
    var age: String = ""
    var profile_pic: String = ""
    var is_read: Int = 0

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
        parcel.readInt() ?: 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(username)
        parcel.writeString(email)
        parcel.writeString(mobile)
        parcel.writeString(gender)
        parcel.writeString(interest)
        parcel.writeString(language)
        parcel.writeString(age)
        parcel.writeString(profile_pic)
        parcel.writeInt(is_read)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MatchesListBean> {
        override fun createFromParcel(parcel: Parcel): MatchesListBean {
            return MatchesListBean(parcel)
        }

        override fun newArray(size: Int): Array<MatchesListBean?> {
            return arrayOfNulls(size)
        }
    }
}