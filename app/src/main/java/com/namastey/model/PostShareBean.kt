package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class PostShareBean() : Parcelable {

    var id: Long = 0
    var user_id: Long  = 0
    var post_id: Int  = 0
    var is_share: Int  = 0

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        user_id = parcel.readLong()
        post_id = parcel.readInt()
        is_share = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(user_id)
        parcel.writeInt(post_id)
        parcel.writeInt(is_share)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PostShareBean> {
        override fun createFromParcel(parcel: Parcel): PostShareBean {
            return PostShareBean(parcel)
        }

        override fun newArray(size: Int): Array<PostShareBean?> {
            return arrayOfNulls(size)
        }
    }
}