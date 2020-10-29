package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class BlockUserListBean() : Parcelable {

    var user_id: Long = 0
    var username: String = ""
    var user_image: String = ""

    constructor(parcel: Parcel) : this() {
        parcel.readLong()
        parcel.readString() ?: ""
        parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(user_id)
        parcel.writeString(username)
        parcel.writeString(user_image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BlockUserListBean> {
        override fun createFromParcel(parcel: Parcel): BlockUserListBean {
            return BlockUserListBean(parcel)
        }

        override fun newArray(size: Int): Array<BlockUserListBean?> {
            return arrayOfNulls(size)
        }
    }
}