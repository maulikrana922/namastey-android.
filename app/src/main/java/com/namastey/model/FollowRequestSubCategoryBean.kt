package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class FollowRequestSubCategoryBean() : Parcelable {

    var name: String = ""
    var start_color: String = ""
    var end_color: String = ""


    constructor(parcel: Parcel) : this() {
        name = parcel.readString() ?: ""
        start_color = parcel.readString() ?: ""
        end_color = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(start_color)
        parcel.writeString(end_color)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FollowRequestSubCategoryBean> {
        override fun createFromParcel(parcel: Parcel): FollowRequestSubCategoryBean {
            return FollowRequestSubCategoryBean(parcel)
        }

        override fun newArray(size: Int): Array<FollowRequestSubCategoryBean?> {
            return arrayOfNulls(size)
        }
    }
}