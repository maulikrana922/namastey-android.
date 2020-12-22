package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class DashboardBean() : Parcelable {

    var id: Long = 0
    var user_id: Long = 0
    var album_id: Long = 0
    var viewers: Long = 0
    var description: String = ""
    var username: String = ""
    var profile_url: String = ""
    var cover_image_url: String = ""
    var video_url: String = ""
    var job: String = ""
    var is_comment: Int = 0
    var share_with: Int = 0
    var is_download: Int = 0
    var comments: Int = 0
    var is_follow: Int = 0
    var is_like: Int = 0
    var profile_pic: ArrayList<String> = ArrayList()
    var isChecked: Int  = 0
    var is_match: Int  = 0
    var user_profile_type: Int = 0
    var is_liked_you: Int = 0
    var share: Int = 0

    constructor(parcel: Parcel) : this() {
        parcel.readLong()
        parcel.readLong()
        parcel.readLong()
        parcel.readLong()
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readInt() ?: 0
        parcel.readInt() ?: 0
        parcel.readInt() ?: 0
        parcel.readInt() ?: 0
        parcel.readInt() ?: 0
        parcel.readInt() ?: 0
        parcel.createStringArrayList() ?: ArrayList()
        parcel.readInt() ?: 0
        parcel.readInt() ?: 0
        parcel.readInt() ?: 0
        parcel.readInt() ?: 0
        parcel.readInt() ?: 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(user_id)
        parcel.writeLong(album_id)
        parcel.writeLong(viewers)
        parcel.writeString(description)
        parcel.writeString(username)
        parcel.writeString(profile_url)
        parcel.writeString(cover_image_url)
        parcel.writeString(video_url)
        parcel.writeString(job)
        parcel.writeInt(is_comment)
        parcel.writeInt(share_with)
        parcel.writeInt(is_download)
        parcel.writeInt(comments)
        parcel.writeInt(is_follow)
        parcel.writeInt(is_like)
        parcel.writeStringList(profile_pic)
        parcel.writeInt(isChecked)
        parcel.writeInt(is_match)
        parcel.writeInt(user_profile_type)
        parcel.writeInt(is_liked_you)
        parcel.writeInt(share)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DashboardBean> {
        override fun createFromParcel(parcel: Parcel): DashboardBean {
            return DashboardBean(parcel)
        }

        override fun newArray(size: Int): Array<DashboardBean?> {
            return arrayOfNulls(size)
        }
    }
}