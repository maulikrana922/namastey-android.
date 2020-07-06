package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class AlbumBean() : Parcelable {

    var id: Long = 0
    var name: String = ""
    var post_video_list: ArrayList<VideoBean> = ArrayList()
    var is_created: Int  = 1

    constructor(parcel: Parcel) : this() {
        parcel.readLong()
        parcel.readString() ?: ""
        parcel.createTypedArrayList(VideoBean) ?: ArrayList()
        is_created = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeTypedList(post_video_list)
        parcel.writeInt(is_created)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AlbumBean> {
        override fun createFromParcel(parcel: Parcel): AlbumBean {
            return AlbumBean(parcel)
        }

        override fun newArray(size: Int): Array<AlbumBean?> {
            return arrayOfNulls(size)
        }
    }
}