package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class LikedUserCountBean() : Parcelable {

    var like_count: Int = 0
    var user_id: Long = 0
    var profile_pic: String = ""

    constructor(parcel: Parcel) : this() {
        like_count = parcel.readInt() ?: 0
        user_id = parcel.readLong()
        profile_pic = parcel.readString() ?: ""

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(like_count)
        parcel.writeLong(user_id)
        parcel.writeString(profile_pic)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LikedUserCountBean> {
        override fun createFromParcel(parcel: Parcel): LikedUserCountBean {
            return LikedUserCountBean(parcel)
        }

        override fun newArray(size: Int): Array<LikedUserCountBean?> {
            return arrayOfNulls(size)
        }
    }
}