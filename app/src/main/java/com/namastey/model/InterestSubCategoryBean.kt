package com.namastey.model

import android.os.Parcel
import android.os.Parcelable

class InterestSubCategoryBean() : Parcelable {

    var id: Int = 0
    var category_id: Int = 0
    var name: String = ""
    var status: Int = 0
    var sub_cat_img: String = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        category_id = parcel.readInt()
        name = parcel.readString() ?: ""
        status = parcel.readInt()
        sub_cat_img = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(category_id)
        parcel.writeString(name)
        parcel.writeInt(status)
        parcel.writeString(sub_cat_img)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InterestSubCategoryBean> {
        override fun createFromParcel(parcel: Parcel): InterestSubCategoryBean {
            return InterestSubCategoryBean(parcel)
        }

        override fun newArray(size: Int): Array<InterestSubCategoryBean?> {
            return arrayOfNulls(size)
        }
    }
}