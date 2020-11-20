package com.namastey.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Notification() : Parcelable {

    @SerializedName("title")
    var title: String = ""

    @SerializedName("link_name")
    var link_name: String = ""

    @SerializedName("link_url")
    var link_url: String = ""

    @SerializedName("message")
    var message: String = ""

    @SerializedName("is_read")
    var is_read: Int = 0

    @SerializedName("created_at")
    var created_at: String = ""

    constructor(parcel: Parcel) : this() {
        this.title = parcel.readString().toString()
        this.link_name = parcel.readString().toString()
        this.link_url = parcel.readString().toString()
        this.message = parcel.readString().toString()
        this.is_read = parcel.readInt()
        this.created_at = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(link_name)
        parcel.writeString(link_url)
        parcel.writeString(message)
        parcel.writeInt(is_read)
        parcel.writeString(created_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Notification> {
        override fun createFromParcel(parcel: Parcel): Notification {
            return Notification(parcel)
        }

        override fun newArray(size: Int): Array<Notification?> {
            return arrayOfNulls(size)
        }
    }

}