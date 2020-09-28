package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class SpotifyBean() : Parcelable {

    var spotify: String = ""

    constructor(parcel: Parcel) : this() {
        parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(spotify)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SpotifyBean> {
        override fun createFromParcel(parcel: Parcel): SpotifyBean {
            return SpotifyBean(parcel)
        }

        override fun newArray(size: Int): Array<SpotifyBean?> {
            return arrayOfNulls(size)
        }
    }
}