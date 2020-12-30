package com.namastey.model

import android.os.Parcel
import android.os.Parcelable

class ChatMessage(
    var message: String,
    var sender: Long,
    var receiver: Long,
    var url: String,
    var timestamp: Long,
    var read: Boolean,
    var unreadCount: Int
) : Parcelable {
    constructor() : this("", -1, -1, "", -1, false,0)

    constructor(parcel: Parcel) : this("", -1, -1, "", -1, false,0) {
        message = parcel.readString() ?: ""
        sender = parcel.readLong() ?: 0
        receiver = parcel.readLong() ?: 0
        url = parcel.readString() ?: ""
        timestamp = parcel.readLong() ?: 0
        read = parcel.readBoolean()
        unreadCount = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(message)
        parcel.writeLong(sender)
        parcel.writeLong(receiver)
        parcel.writeString(url)
        parcel.writeLong(timestamp)
        parcel.writeByte(if (read) 1 else 0)
        parcel.writeInt(unreadCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatMessage> {
        override fun createFromParcel(parcel: Parcel): ChatMessage {
            return ChatMessage(parcel)
        }

        override fun newArray(size: Int): Array<ChatMessage?> {
            return arrayOfNulls(size)
        }
    }


}