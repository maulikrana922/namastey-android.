package com.namastey.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*


class FollowRequestBean() : Parcelable {

    var id: Long = 0
    var follow_id: Long = 0
    var username: String = ""
    var job: String = ""
    var following_user_id: Long = 0
    var profile_url: String = ""
    var updated_at: String = ""
    var sub_cat_details: ArrayList<FollowRequestSubCategoryBean> = ArrayList()

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        follow_id = parcel.readLong()
        username = parcel.readString() ?: ""
        job = parcel.readString() ?: ""
        following_user_id = parcel.readLong()
        profile_url = parcel.readString() ?: ""
        updated_at = parcel.readString() ?: ""
        sub_cat_details = parcel.createTypedArrayList(FollowRequestSubCategoryBean) ?: ArrayList()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(follow_id)
        parcel.writeString(username)
        parcel.writeString(job)
        parcel.writeLong(following_user_id)
        parcel.writeString(profile_url)
        parcel.writeString(updated_at)
        parcel.writeTypedList(sub_cat_details)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FollowRequestBean> {
        override fun createFromParcel(parcel: Parcel): FollowRequestBean {
            return FollowRequestBean(parcel)
        }

        override fun newArray(size: Int): Array<FollowRequestBean?> {
            return arrayOfNulls(size)
        }
    }
}