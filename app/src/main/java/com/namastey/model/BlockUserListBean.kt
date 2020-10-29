package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class BlockUserListBean() : Parcelable {

    var user_id: Long = 0
    var username: String = ""
    var user_image: String = ""
    var job: String = ""

    constructor(parcel: Parcel) : this() {
        user_id = parcel.readLong()
        username = parcel.readString() ?: ""
        user_image = parcel.readString() ?: ""
        job = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(user_id)
        parcel.writeString(username)
        parcel.writeString(user_image)
        parcel.writeString(job)
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