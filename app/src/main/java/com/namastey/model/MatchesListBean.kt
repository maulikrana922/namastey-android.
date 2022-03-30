package com.namastey.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*


class MatchesListBean() : Parcelable {

    var id: Long = 0
    var username: String = ""
    var casual_name: String = ""
    var email: String = ""
    var mobile: String = ""
    var gender: String = ""
    var interest: String = ""
    var language: String = ""
    var age: String = ""
    var profile_pic: String = ""
    var is_read: Int = 0
    var sub_cat_details: ArrayList<FollowRequestSubCategoryBean> = ArrayList()
    var chatMessage: ChatMessage = ChatMessage()
    var is_match: Int = 0
    var is_block: Int = 0
    var is_follow_me: Int = 0
    var is_notification: Int = 0
    var is_super_message_used:Int=0
    var who_can_send_message: Int = 0

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        username = parcel.readString() ?: ""
        casual_name = parcel.readString() ?: ""
        email = parcel.readString() ?: ""
        mobile = parcel.readString() ?: ""
        gender = parcel.readString() ?: ""
        interest = parcel.readString() ?: ""
        language = parcel.readString() ?: ""
        age = parcel.readString() ?: ""
        profile_pic = parcel.readString() ?: ""
        is_read = parcel.readInt() ?: 0
        sub_cat_details = parcel.createTypedArrayList(FollowRequestSubCategoryBean) ?: ArrayList()
        chatMessage = parcel.readValue(ChatMessage::class.java.classLoader) as ChatMessage
        is_match = parcel.readInt() ?: 0
        is_block = parcel.readInt() ?: 0
        is_follow_me = parcel.readInt() ?: 0
        is_notification = parcel.readInt() ?: 0
        is_super_message_used=parcel.readInt() ?: 0
        who_can_send_message = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(username)
        parcel.writeString(casual_name)
        parcel.writeString(email)
        parcel.writeString(mobile)
        parcel.writeString(gender)
        parcel.writeString(interest)
        parcel.writeString(language)
        parcel.writeString(age)
        parcel.writeString(profile_pic)
        parcel.writeInt(is_read)
        parcel.writeTypedList(sub_cat_details)
        parcel.writeValue(chatMessage)
        parcel.writeInt(is_match)
        parcel.writeInt(is_block)
        parcel.writeInt(is_follow_me)
        parcel.writeInt(is_notification)
        parcel.writeInt(is_super_message_used)
        parcel.writeInt(who_can_send_message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MatchesListBean> {
        override fun createFromParcel(parcel: Parcel): MatchesListBean {
            return MatchesListBean(parcel)
        }

        override fun newArray(size: Int): Array<MatchesListBean?> {
            return arrayOfNulls(size)
        }
    }
}