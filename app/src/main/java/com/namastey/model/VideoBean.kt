package com.namastey.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*


class VideoBean() : Parcelable {

    var id: Long = 0
    var video_name: String = ""
    var video_url: String = ""
    var description: String = ""
    var cover_image_url: String = ""
    var album_id: Long = 0
    var album_name: String = ""
    var share_with = 0
    var is_comment: Int = 0
    var is_like: Int = 0
    var viewers = 0
    var comments = 0
    var profile_pic: ArrayList<String> = ArrayList()
    var user_id: Long = 0
    var username: String = ""
    var casual_name: String = ""
    var profile_url: String = ""
    var is_download: Int = 0
    var share: Int = 0
    var job: String = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        video_name = parcel.readString() ?: ""
        video_url = parcel.readString() ?: ""
        description = parcel.readString() ?: ""
        cover_image_url = parcel.readString() ?: ""
        album_id = parcel.readLong()
        album_name = parcel.readString() ?: ""
        share_with = parcel.readInt()
        is_comment = parcel.readInt()
        is_like = parcel.readInt()
        viewers = parcel.readInt()
        comments = parcel.readInt()
        profile_pic = parcel.createStringArrayList() ?: ArrayList()
        user_id = parcel.readLong()
        username = parcel.readString() ?: ""
        casual_name = parcel.readString() ?: ""
        profile_url = parcel.readString() ?: ""
        is_download = parcel.readInt()
        share = parcel.readInt()
        job =  parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(video_name)
        parcel.writeString(video_url)
        parcel.writeString(description)
        parcel.writeString(cover_image_url)
        parcel.writeLong(album_id)
        parcel.writeString(album_name)
        parcel.writeInt(share_with)
        parcel.writeInt(is_comment)
        parcel.writeInt(is_like)
        parcel.writeInt(viewers)
        parcel.writeInt(comments)
        parcel.writeStringList(profile_pic)
        parcel.writeLong(user_id)
        parcel.writeString(username)
        parcel.writeString(casual_name)
        parcel.writeString(profile_url)
        parcel.writeInt(is_download)
        parcel.writeInt(share)
        parcel.writeString(job)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoBean> {
        override fun createFromParcel(parcel: Parcel): VideoBean {
            return VideoBean(parcel)
        }

        override fun newArray(size: Int): Array<VideoBean?> {
            return arrayOfNulls(size)
        }
    }
}