package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class FollowRequestBean() : Parcelable {

    var follow_id: Long = 0
    var username: String = ""
    var following_user_id: Long = 0
    var following_user_profile_pic: String = ""
    var updated_at: String = ""


    constructor(parcel: Parcel) : this() {
        parcel.readLong()
        parcel.readString() ?: ""
        parcel.readLong()
        parcel.readString() ?: ""
        parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(follow_id)
        parcel.writeString(username)
        parcel.writeLong(following_user_id)
        parcel.writeString(following_user_profile_pic)
        parcel.writeString(updated_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FollowRequestBean> {
        override fun createFromParcel(parcel: Parcel): FollowRequestBean {
            return FollowRequestBean(parcel)
        }

        override fun newArray(size: Int): Array<FollowRequestBean?> {
            return arrayOfNulls(size)
        }
    }
}