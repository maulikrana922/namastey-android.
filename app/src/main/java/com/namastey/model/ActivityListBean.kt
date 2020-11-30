package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class ActivityListBean() : Parcelable {

    //for Followers
    var follow_id: Int = 0
    var following_user_id: Long = 0
    var following_user_profile_pic: String = ""
    var updated_at: String = ""
    var follow_message: String = ""
    var is_follow: Int = 0

    var user_id: Long = 0

    //For Like
    var like_id: Int = 0
    var like_user_profile_pic: String = ""
    var like_message: String = ""

    //For Comment
    var comment_id: Int = 0
    var post_video_url: String = ""
    var cover_image_url: String = ""
    var comment_user_profile_pic: String = ""
    var comment_message: String = ""


    //For Mentions
    var mentions_id: Int = 0
    var mentions_user_profile_pic: String = ""
    var menstion_message: String = ""

    constructor(parcel: Parcel) : this() {
        follow_id = parcel.readInt()
        following_user_id = parcel.readLong()
        following_user_profile_pic = parcel.readString() ?: ""
        follow_message = parcel.readString() ?: ""
        updated_at = parcel.readString() ?: ""

        user_id = parcel.readLong()
        like_id = parcel.readInt()
        like_user_profile_pic = parcel.readString() ?: ""
        like_message = parcel.readString() ?: ""

        comment_id = parcel.readInt()
        post_video_url = parcel.readString() ?: ""
        cover_image_url = parcel.readString() ?: ""
        comment_user_profile_pic = parcel.readString() ?: ""
        comment_message = parcel.readString() ?: ""

        mentions_id = parcel.readInt()
        mentions_user_profile_pic = parcel.readString() ?: ""
        menstion_message = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(follow_id)
        parcel.writeLong(following_user_id)
        parcel.writeString(following_user_profile_pic)
        parcel.writeString(follow_message)
        parcel.writeString(updated_at)

        parcel.writeLong(user_id)
        parcel.writeInt(like_id)
        parcel.writeString(like_user_profile_pic)
        parcel.writeString(like_message)

        parcel.writeInt(comment_id)
        parcel.writeString(post_video_url)
        parcel.writeString(cover_image_url)
        parcel.writeString(comment_user_profile_pic)
        parcel.writeString(comment_message)

        parcel.writeInt(mentions_id)
        parcel.writeString(mentions_user_profile_pic)
        parcel.writeString(menstion_message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ActivityListBean> {
        override fun createFromParcel(parcel: Parcel): ActivityListBean {
            return ActivityListBean(parcel)
        }

        override fun newArray(size: Int): Array<ActivityListBean?> {
            return arrayOfNulls(size)
        }
    }
}