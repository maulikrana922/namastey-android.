package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class MentionListBean() : Parcelable {

    var user_id: Long = 0
    var email: String = ""
    var profile_url: String = ""
    var username: String = ""
    var casual_name: String = ""
    var mobile: String = ""
    var is_completly_signup: Int = 0
    var is_verified: Int = 0
    var verified_at: String = ""
    var job: String = ""


    constructor(parcel: Parcel) : this() {
        parcel.readLong()
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readInt() ?: 0
        parcel.readInt() ?: 0
        parcel.readString() ?: ""
        parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(user_id)
        parcel.writeString(email)
        parcel.writeString(profile_url)
        parcel.writeString(username)
        parcel.writeString(casual_name)
        parcel.writeInt(is_completly_signup)
        parcel.writeInt(is_verified)
        parcel.writeString(verified_at)
        parcel.writeString(job)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MentionListBean> {
        override fun createFromParcel(parcel: Parcel): MentionListBean {
            return MentionListBean(parcel)
        }

        override fun newArray(size: Int): Array<MentionListBean?> {
            return arrayOfNulls(size)
        }
    }
}