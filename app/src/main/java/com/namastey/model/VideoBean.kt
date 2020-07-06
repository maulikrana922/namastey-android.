package com.namastey.model

import android.R.attr.name
import android.os.Parcel
import android.os.Parcelable


class VideoBean() : Parcelable {

    var id: Long = 0
    var video_name: String = ""
    var video_url: String = ""
    var description: String = ""
    var cover_image_url: String = ""
    var album_id: Long = 0
    var share_with = 0

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        video_name = parcel.readString() ?: ""
        video_url = parcel.readString() ?: ""
        description = parcel.readString() ?: ""
        cover_image_url = parcel.readString() ?: ""
        album_id = parcel.readLong()
        share_with = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(video_name)
        parcel.writeString(video_url)
        parcel.writeString(description)
        parcel.writeString(cover_image_url)
        parcel.writeLong(album_id)
        parcel.writeInt(share_with)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoBean> {
        override fun createFromParcel(parcel: Parcel): VideoBean {
            return VideoBean(parcel)
        }

        override fun newArray(size: Int): Array<VideoBean?> {
            return arrayOfNulls(size)
        }
    }
}