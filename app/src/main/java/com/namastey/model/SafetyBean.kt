package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class SafetyBean() : Parcelable {

    var id: Int = 0
    var user_id: Long = 0
    var is_download: Int = 0
    var is_followers: Int = 0
    var is_suggest: Int = 0
    var who_can_comment: Int = 0
    var who_can_send_message: Int = 0

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt() ?: 0
        user_id = parcel.readLong()
        is_download = parcel.readInt() ?: 0
        is_followers = parcel.readInt() ?: 0
        is_suggest = parcel.readInt() ?: 0
        who_can_comment = parcel.readInt() ?: 0
        who_can_send_message = parcel.readInt() ?: 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeLong(user_id)
        parcel.writeInt(is_download)
        parcel.writeInt(is_followers)
        parcel.writeInt(is_suggest)
        parcel.writeInt(who_can_comment)
        parcel.writeInt(who_can_send_message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SafetyBean> {
        override fun createFromParcel(parcel: Parcel): SafetyBean {
            return SafetyBean(parcel)
        }

        override fun newArray(size: Int): Array<SafetyBean?> {
            return arrayOfNulls(size)
        }
    }
}