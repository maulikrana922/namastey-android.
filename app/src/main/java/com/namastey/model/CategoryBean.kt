package com.namastey.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*


class CategoryBean() : Parcelable {

    var id: Int = 0
    var name: String = ""
    var sub_category: ArrayList<CategoryBean> = ArrayList()
    var is_selected: Int  = 0
    @SerializedName("start_color")
    var startColor: String = ""
    @SerializedName("end_color")
    var endColor: String = ""
    var sub_cat_img: String = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        name = parcel.readString() ?: ""
        sub_category = parcel.createTypedArrayList(CREATOR) ?: ArrayList()
        is_selected = parcel.readInt()
        startColor = parcel.readString() ?: ""
        endColor = parcel.readString() ?: ""
        sub_cat_img = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeTypedList(sub_category)
        parcel.writeInt(is_selected)
        parcel.writeString(startColor)
        parcel.writeString(endColor)
        parcel.writeString(sub_cat_img)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CategoryBean> {
        override fun createFromParcel(parcel: Parcel): CategoryBean {
            return CategoryBean(parcel)
        }

        override fun newArray(size: Int): Array<CategoryBean?> {
            return arrayOfNulls(size)
        }
    }
}