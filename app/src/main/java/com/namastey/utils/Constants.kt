package com.namastey.utils

object Constants {
    enum class Gender {
        male, female, other
    }

    const val API_KEY = "Authorization"
    const val HVALUE = "L@titude2018"

    //    Live url
    const val BASE = "https://testyourapp.online/namasteyapp/api/"

    //    api end-point
    const val REGISTER = "register"
    const val VERIFY_OTP = "verify-otp"
    const val GET_COUNTRY = "countries-list"
    const val GET_VIDEO_LANGUAGE = "video-language-list"
    const val GET_INTEREST_LIST = "interests-list"
    const val SOCIAL_LOGIN = "social-login"
    const val REGISTER_GUEST = "register-guest"
    const val GET_USER_DETAIL = "user-detail"
    const val GET_CATEGORY_LIST = "category-list"

    const val LOGIN = "login"
    const val LOGOUT = "logout"
    const val PASSWORD = "password"
    const val DEVICE_ID = "device_id"
    const val DEVICE_TYPE = "device_type"
    const val LATITUDE = "latitude"
    const val LONGITUDE = "longitude"

//    api response error code
    const val OK = 200

    const val TIME_OUT: Long = 60
    const val CACHE_TIME = 432000

    const val MULI_REGULAR = 0
    const val MULI_LIGHT = 1
    const val MULI_SEMI_BOLD = 2
    const val MULI_BLACK = 3
    const val MULI_EXTRA_BOLD = 4

    const val INVALID_SESSION_ERROR_CODE = 201

    //    Fragment tag
    const val SIGNUP_WITH_PHONE_FRAGMENT = "SignupWithPhoneFragment"
    const val OTP_FRAGMENT = "OTPFragment"
    const val SELECT_GENDER_FRAGMENT = "SelectGenderFragment"
    const val VIDEO_LANGUAG_EFRAGMENT = "VideoLanguageFragment"
    const val CHOOSE_INTEREST_FRAGMENT = "ChooseInterestFragment"
    const val COUNTRY_FRAGMENT = "CountryFragment"
    const val FIND_FRIEND_FRAGMENT = "FindFriendFragment"

    //    Session key
    const val KEY_USER_ID = "KEY_USER_ID"
    const val KEY_USER_NAME = "KEY_USER_NAME"
    const val KEY_IS_LOGIN = "KEY_IS_LOGIN"
    const val KEY_SESSION_TOKEN = "KEY_SESSION_TOKEN"
    const val KEY_LOGIN_TYPE = "KEY_LOGIN_TYPE"
    const val KEY_INTERNET_AVAILABLE = "KEY_INTERNET_AVAILABLE"
    const val KEY_IS_VERIFIED_USER = "KEY_IS_VERIFIED_USER"
    const val KEY_GENDER = "KEY_GENDER"


    //    field key
    const val MOBILE = "mobile"
    const val EMAIL = "email"
    const val USERNAME = "username"
    const val OTP = "otp"
    const val FACEBOOK = "facebook"
    const val GOOGLE = "google"
    const val ANDROID = "android"
    const val PROVIDER = "provider"
    const val PROVIDER_ID = "provider_id"
    const val USER_UNIQUEID = "user_uniqueId"
    const val GENDER = "gender"
    const val DATE_OF_BIRTH = "date_of_birth"
    const val LANGUAGE = "language"
    const val INTEREST = "interest"
    const val COUNTRY_CODE = "countryCode"

//    Other constants
    const val MIN_CHOOSE_INTEREST = 3
    const val DATE_FORMATE_DISPLAY = "dd/MM/yyyy"
    const val DATE_FORMATE_API = "yyyy-MM-dd"
    const val RC_SIGN_IN = 100

}
