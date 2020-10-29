package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class NotificationOnOffBean() : Parcelable {

    var id: Int = 0
    var user_id: Long = 0
    var is_mentions: Int = 0
    var is_matches: Int = 0
    var is_follow: Int = 0
    var is_comment: Int = 0
    var is_suggest: Int = 0

    constructor(parcel: Parcel) : this() {
        parcel.readInt() ?: 0
        parcel.readLong()
        parcel.readInt() ?: 0
        parcel.readInt() ?: 0
        parcel.readInt() ?: 0
        parcel.readInt() ?: 0
        parcel.readInt() ?: 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeLong(user_id)
        parcel.writeInt(is_mentions)
        parcel.writeInt(is_matches)
        parcel.writeInt(is_follow)
        parcel.writeInt(is_comment)
        parcel.writeInt(is_suggest)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NotificationOnOffBean> {
        override fun createFromParcel(parcel: Parcel): NotificationOnOffBean {
            return NotificationOnOffBean(parcel)
        }

        override fun newArray(size: Int): Array<NotificationOnOffBean?> {
            return arrayOfNulls(size)
        }
    }
}