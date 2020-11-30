package com.namastey.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


class ProfileBean() : Parcelable {

    var user_id: Long = 0
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
    var is_follow: Int = 0
    var is_hide: Int = 0
    var user_profile_type: Int = 0
    var distance: String = ""
    var gender: String = ""
    var category: ArrayList<CategoryBean> = ArrayList()
    var education: ArrayList<EducationBean> = ArrayList()
    var jobs: ArrayList<JobBean> = ArrayList()
    var social_accounts: ArrayList<SocialAccountBean> = ArrayList()
    var interest: ArrayList<InterestBean> = ArrayList()
    var albums: ArrayList<AlbumBean> = ArrayList()
    var age: Int = 0
    var is_like: Int = 0
    @SerializedName("notification_tage")
    var notificationBean : ArrayList<NotificationOnOffBean> = ArrayList()

    @SerializedName("safety_tag")
    var safetyBean : SafetyBean = SafetyBean()

    @SerializedName("language")
    var languageBean : ArrayList<VideoLanguageBean> = ArrayList()

    constructor(parcel: Parcel) : this() {
        user_id = parcel.readLong()
        email = parcel.readString() ?: ""
        name = parcel.readString() ?: ""
        username = parcel.readString() ?: ""
        profileUrl = parcel.readString() ?: ""
        about_me = parcel.readString() ?: ""
        is_completly_signup = parcel.readInt() ?: 0
        min_age = parcel.readInt() ?: 0
        max_age = parcel.readInt() ?: 0
        following = parcel.readInt() ?: 0
        followers = parcel.readInt() ?: 0
        viewers = parcel.readInt() ?: 0
        interest_in_gender = parcel.readInt() ?: 0
        is_follow = parcel.readInt() ?: 0
        is_hide = parcel.readInt() ?: 0
        user_profile_type = parcel.readInt() ?: 0
        distance = parcel.readString() ?: ""
        gender = parcel.readString() ?: ""
        category = parcel.createTypedArrayList(CategoryBean) ?: ArrayList()
        education = parcel.createTypedArrayList(EducationBean) ?: ArrayList()
        jobs = parcel.createTypedArrayList(JobBean) ?: ArrayList()
        social_accounts = parcel.createTypedArrayList(SocialAccountBean) ?: ArrayList()
        interest = parcel.createTypedArrayList(InterestBean) ?: ArrayList()
        albums = parcel.createTypedArrayList(AlbumBean) ?: ArrayList()
        age = parcel.readInt() ?: 0
        is_like = parcel.readInt() ?: 0
        notificationBean = parcel.createTypedArrayList(NotificationOnOffBean) ?: ArrayList()
       // safetyBean = parcel.createTypedArrayList(SafetyBean) ?: SafetyBean()
        safetyBean = parcel.readValue(SafetyBean::class.java.classLoader) as SafetyBean
        languageBean = parcel.createTypedArrayList(VideoLanguageBean) ?: ArrayList()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(user_id)
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
        parcel.writeInt(is_follow)
        parcel.writeInt(is_hide)
        parcel.writeInt(user_profile_type)
        parcel.writeString(distance)
        parcel.writeString(gender)
        parcel.writeTypedList(category)
        parcel.writeTypedList(education)
        parcel.writeTypedList(jobs)
        parcel.writeTypedList(social_accounts)
        parcel.writeTypedList(interest)
        parcel.writeTypedList(albums)
        parcel.writeInt(age)
        parcel.writeInt(is_like)
        parcel.writeTypedList(notificationBean)
        //parcel.writeTypedList(safetyBean)
        parcel.writeValue(safetyBean)
        parcel.writeTypedList(languageBean)
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