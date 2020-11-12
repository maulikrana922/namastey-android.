package com.namastey.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


class CommentBean() : Parcelable {

    var id: Long = 0
    var user_id: Long = 0
    @SerializedName("post_video_id")
    var postVideoId: Long = 0
    var comment: String = ""
    var username: String = ""
    var profile_pic: String = ""
    var user_profile_type : String = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        user_id = parcel.readLong()
        postVideoId = parcel.readLong()
        comment = parcel.readString() ?: ""
        username = parcel.readString() ?: ""
        profile_pic = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(user_id)
        parcel.writeLong(postVideoId)
        parcel.writeString(comment)
        parcel.writeString(username)
        parcel.writeString(profile_pic)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CommentBean> {
        override fun createFromParcel(parcel: Parcel): CommentBean {
            return CommentBean(parcel)
        }

        override fun newArray(size: Int): Array<CommentBean?> {
            return arrayOfNulls(size)
        }
    }
}