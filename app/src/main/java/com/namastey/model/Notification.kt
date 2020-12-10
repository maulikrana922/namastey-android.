package com.namastey.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Notification() : Parcelable {

    @SerializedName("notification_count")
    var notificationCount: String = ""

    @SerializedName("is_notification_type")
    var isNotificationType:String = ""

    @SerializedName("NT")
    var nt: String = ""

    @SerializedName("alert")
    var alert: String = ""

    @SerializedName("title")
    var title: String = ""

    @SerializedName("message")
    var message: String = ""

    @SerializedName("created_at")
    var createdAt: String = ""

    @SerializedName("post_image")
    var postImage: String = ""

    @SerializedName("is_read")
    var isRead: String = ""


    constructor(parcel: Parcel) : this() {
        this.notificationCount = parcel.readString().toString()
        this.isNotificationType = parcel.readString().toString()
        this.nt =  parcel.readString().toString()
        this.alert = parcel.readString().toString()
        this.title =  parcel.readString().toString()
        this.message = parcel.readString().toString()
        this.createdAt = parcel.readString().toString()
        this.postImage = parcel.readString().toString()
        this.isRead = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(notificationCount)
        parcel.writeString(isNotificationType)
        parcel.writeString(nt)
        parcel.writeString(alert)
        parcel.writeString(title)
        parcel.writeString(message)
        parcel.writeString(createdAt)
        parcel.writeString(postImage)
        parcel.writeString(isRead)
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