package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class VideoLanguageBean() : Parcelable {

    var id: Int = 0
    var first_language: String = ""
    var second_language: String = ""

    constructor(parcel: Parcel) : this() {
        parcel.readInt()
        parcel.readString() ?: ""
        parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(first_language)
        parcel.writeString(second_language)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoLanguageBean> {
        override fun createFromParcel(parcel: Parcel): VideoLanguageBean {
            return VideoLanguageBean(parcel)
        }

        override fun newArray(size: Int): Array<VideoLanguageBean?> {
            return arrayOfNulls(size)
        }
    }
}