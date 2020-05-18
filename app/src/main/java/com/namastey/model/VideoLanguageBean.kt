package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class VideoLanguageBean() : Parcelable {

    var id: Int = 0
    var video_lang: String = ""
    var video_lang_name: String = ""

    constructor(parcel: Parcel) : this() {
        parcel.readInt()
        parcel.readString() ?: ""
        parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(video_lang)
        parcel.writeString(video_lang_name)
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