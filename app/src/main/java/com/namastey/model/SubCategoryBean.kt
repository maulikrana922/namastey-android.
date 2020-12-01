package com.namastey.model

import android.os.Parcel
import android.os.Parcelable

class SubCategoryBean() : Parcelable {

    var id: Int = 0
    var name: String = ""
    var status: Int = 0
    var sub_cat_img: String = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        name = parcel.readString() ?: ""
        status = parcel.readInt()
        sub_cat_img = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(status)
        parcel.writeString(sub_cat_img)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SubCategoryBean> {
        override fun createFromParcel(parcel: Parcel): SubCategoryBean {
            return SubCategoryBean(parcel)
        }

        override fun newArray(size: Int): Array<SubCategoryBean?> {
            return arrayOfNulls(size)
        }
    }
}