package com.namastey.model

import android.os.Parcel
import android.os.Parcelable


class BoostBean() : Parcelable {
    var id: Int = 0
    var user_id: Int = 0
    var number_of_boost_available: Int = 0
    var created_at: String = ""
    var updated_at: String = ""
    var deleted_at: Int = 0


    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        user_id = parcel.readInt()
        number_of_boost_available = parcel.readInt()
        created_at = parcel.readString() ?: ""
        updated_at = parcel.readString() ?: ""
        deleted_at = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(user_id)
        parcel.writeInt(number_of_boost_available)
        parcel.writeString(created_at)
        parcel.writeString(updated_at)
        parcel.writeInt(deleted_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BoostBean> {
        override fun createFromParcel(parcel: Parcel): BoostBean {
            return BoostBean(parcel)
        }

        override fun newArray(size: Int): Array<BoostBean?> {
            return arrayOfNulls(size)
        }
    }
}