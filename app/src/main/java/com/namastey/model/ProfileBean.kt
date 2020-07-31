package com.namastey.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Job


class ProfileBean() : Parcelable {

    //    var id: Int = 0
    var email: String = ""
    var name: String = ""
    var username: String = ""
    @SerializedName("profile_url")
    var profileUrl: String = ""
    @SerializedName("tagline_or_about_detail")
    var about_me: String = ""
    var is_completly_signup: Int = 0
    var min_age: Int = 0
    var max_age: Int = 0
    var following: Int = 0
    var followers: Int = 0
    var viewers: Int = 0
    var interest_in_gender: Int = 0
    var category: ArrayList<CategoryBean> = ArrayList()
    var education: ArrayList<EducationBean> = ArrayList()
    var jobs: ArrayList<JobBean> = ArrayList()
    var social_accounts: ArrayList<SocialAccountBean> = ArrayList()


    constructor(parcel: Parcel) : this() {
//        parcel.readInt()
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readString() ?: ""
        parcel.readInt() ?: 0
        parcel.readInt() ?: 0
        parcel.readInt() ?: 0
        parcel.readInt() ?: 0
        parcel.readInt() ?: 0
        parcel.readInt() ?: 0
        parcel.readInt() ?: 0
        parcel.createTypedArrayList(CategoryBean) ?: ArrayList()
        parcel.createTypedArrayList(EducationBean) ?: ArrayList()
        parcel.createTypedArrayList(JobBean) ?: ArrayList()
        parcel.createTypedArrayList(SocialAccountBean) ?: ArrayList()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeInt(id)
        parcel.writeString(email)
        parcel.writeString(name)
        parcel.writeString(username)
        parcel.writeString(profileUrl)
        parcel.writeString(about_me)
        parcel.writeInt(is_completly_signup)
        parcel.writeInt(min_age)
        parcel.writeInt(max_age)
        parcel.writeInt(following)
        parcel.writeInt(followers)
        parcel.writeInt(viewers)
        parcel.writeInt(interest_in_gender)
        parcel.writeTypedList(category)
        parcel.writeTypedList(education)
        parcel.writeTypedList(jobs)
        parcel.writeTypedList(social_accounts)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProfileBean> {
        override fun createFromParcel(parcel: Parcel): ProfileBean {
            return ProfileBean(parcel)
        }

        override fun newArray(size: Int): Array<ProfileBean?> {
            return arrayOfNulls(size)
        }
    }
}