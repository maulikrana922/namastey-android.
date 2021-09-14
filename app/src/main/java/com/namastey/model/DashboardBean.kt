package com.namastey.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*


class DashboardBean() : Parcelable {

    var id: Long = 0
    var user_id: Long = 0
    var album_id: Long = 0
    var viewers: Long = 0
    var description: String = ""
    var username: String = ""
    var casual_name: String = ""
    var profile_url: String = ""
    var cover_image_url: String = ""
    var video_url: String = ""
    var job: String = ""
    var is_comment: Int = 0     // comment on/off when user post video
    var who_can_comment: Int = 0 // Set from account setting to all videos
    var is_follow_me: Int = 0      // This is for that video user follow login user or not
    var share_with: Int = 0
    var is_download: Int = 0
    var comments: Int = 0       // Total number of comment
    var is_follow: Int = 0
    var is_like: Int = 0
    var profile_pic: ArrayList<String> = ArrayList()
    var isChecked: Int  = 0
    var is_match: Int  = 0
    var user_profile_type: Int = 0
    var is_liked_you: Int = 0
    var share: Int = 0
    var is_share: Int = 0    // User profile share enable or disable
    var is_saved: Int = 0    // Already saved video or not
    var is_reported: Int = 0
    var is_report_me: Int = 0    // Already report user or not
    // Already report user or not
    var who_can_send_message: Int = 0

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
        parcel.readString() ?: ""
        parcel.readInt() ?: 0
        parcel.readInt() ?: 0
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
        parcel.writeString(casual_name)
        parcel.writeString(profile_url)
        parcel.writeString(cover_image_url)
        parcel.writeString(video_url)
        parcel.writeString(job)
        parcel.writeInt(is_comment)
        parcel.writeInt(who_can_comment)
        parcel.writeInt(is_follow_me)
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
        parcel.writeInt(is_share)
        parcel.writeInt(is_saved)
        parcel.writeInt(is_reported)
        parcel.writeInt(is_report_me)
        parcel.writeInt(who_can_send_message)
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