package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class FollowRequestBean() : Parcelable {

    var id: Long = 0
    var follow_id: Long = 0
    var username: String = ""
    var job: String = ""
    var following_user_id: Long = 0
    var profile_url: String = ""
    var updated_at: String = ""


    constructor(parcel: Parcel) : this() {
        parcel.readLong()
        parcel.readLong()
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readLong()
        parcel.readString() ?: ""
        parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(follow_id)
        parcel.writeString(username)
        parcel.writeString(job)
        parcel.writeLong(following_user_id)
        parcel.writeString(profile_url)
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