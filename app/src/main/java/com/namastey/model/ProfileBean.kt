package com.namastey.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*


class ProfileBean() : Parcelable {

    var user_id: Long = 0
    var email: String = ""
    var name: String = ""
    var username: String = ""
    var casual_name: String = ""

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
    var is_follow: Int = 0  // This is for login user follow particular user or not
    var is_follow_me: Int = 0  // This is for that particular user follow login user or not
    var is_hide: Int = 0
    var user_profile_type: Int = 0
    var is_global: Int = 0
    var distance: String = ""
    var gender: String = ""
    var category: ArrayList<CategoryBean> = ArrayList()
    var education: String = ""

    // var jobs: ArrayList<JobBean> = ArrayList()
    var jobs: String = ""
    var social_accounts: ArrayList<SocialAccountBean> = ArrayList()

    //  var interest: ArrayList<InterestBean> = ArrayList()
    var albums: ArrayList<AlbumBean> = ArrayList()
    var age: Int = 0
    var is_like: Int = 0
    var is_block: Int = 0
    var is_match: Int = 0

    @SerializedName("notification_tage")
    var notificationBean: ArrayList<NotificationOnOffBean> = ArrayList()

    @SerializedName("safety_tag")
    var safetyBean: SafetyBean = SafetyBean()

    @SerializedName("language")
    var languageBean: ArrayList<VideoLanguageBean> = ArrayList()

    var sub_cat_tag: ArrayList<InterestSubCategoryBean> = ArrayList()

    constructor(parcel: Parcel) : this() {
        user_id = parcel.readLong()
        email = parcel.readString() ?: ""
        name = parcel.readString() ?: ""
        username = parcel.readString() ?: ""
        casual_name = parcel.readString() ?: ""
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
        is_follow_me = parcel.readInt() ?: 0
        is_hide = parcel.readInt() ?: 0
        user_profile_type = parcel.readInt() ?: 0
        is_global = parcel.readInt() ?: 0
        distance = parcel.readString() ?: ""
        gender = parcel.readString() ?: ""
        category = parcel.createTypedArrayList(CategoryBean) ?: ArrayList()
        education = parcel.readString() ?: ""
        jobs = parcel.readString() ?: ""
        social_accounts = parcel.createTypedArrayList(SocialAccountBean) ?: ArrayList()
        //  interest = parcel.createTypedArrayList(InterestBean) ?: ArrayList()
        albums = parcel.createTypedArrayList(AlbumBean) ?: ArrayList()
        age = parcel.readInt() ?: 0
        is_like = parcel.readInt() ?: 0
        is_block = parcel.readInt() ?: 0
        is_match = parcel.readInt() ?: 0
        notificationBean = parcel.createTypedArrayList(NotificationOnOffBean) ?: ArrayList()
        // safetyBean = parcel.createTypedArrayList(SafetyBean) ?: SafetyBean()
        safetyBean = parcel.readValue(SafetyBean::class.java.classLoader) as SafetyBean
        languageBean = parcel.createTypedArrayList(VideoLanguageBean) ?: ArrayList()
        sub_cat_tag = parcel.createTypedArrayList(InterestSubCategoryBean) ?: ArrayList()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(user_id)
        parcel.writeString(email)
        parcel.writeString(name)
        parcel.writeString(username)
        parcel.writeString(casual_name)
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
        parcel.writeInt(is_follow_me)
        parcel.writeInt(is_hide)
        parcel.writeInt(user_profile_type)
        parcel.writeInt(is_global)
        parcel.writeString(distance)
        parcel.writeString(gender)
        parcel.writeTypedList(category)
        parcel.writeString(education)
        parcel.writeString(jobs)
        parcel.writeTypedList(social_accounts)
        //   parcel.writeTypedList(interest)
        parcel.writeTypedList(albums)
        parcel.writeInt(age)
        parcel.writeInt(is_like)
        parcel.writeInt(is_block)
        parcel.writeInt(is_match)
        parcel.writeTypedList(notificationBean)
        //parcel.writeTypedList(safetyBean)
        parcel.writeValue(safetyBean)
        parcel.writeTypedList(languageBean)
        parcel.writeTypedList(sub_cat_tag)
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