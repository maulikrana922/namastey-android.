package com.namastey.model

import android.os.Parcel
import android.os.Parcelable

data class PhoneContactData(
    val id: String,
    val number: String,
    val name: String
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(number)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PhoneContactData> {
        override fun createFromParcel(parcel: Parcel): PhoneContactData {
            return PhoneContactData(parcel)
        }

        override fun newArray(size: Int): Array<PhoneContactData?> {
            return arrayOfNulls(size)
        }
    }

}