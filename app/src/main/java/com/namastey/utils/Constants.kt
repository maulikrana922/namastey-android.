package com.namastey.utils

import android.os.Environment
import java.io.File

object Constants {
    enum class Gender {
        male, female, other
    }

    object TwitterConstants{
        var CONSUMER_KEY = "66lJfE8259OsFIkRtSdZhXRid"
        var CONSUMER_SECRET = "fY7rgML5bVkY8X9ZNdnZtNIr6CpRfjmn2QTiRdsBeSAhUOZyau"
        var CALLBACK_URL = "twittersdk://"
    }

    const val API_KEY = "Authorization"
    const val HVALUE = "L@titude2018"

    //    Live url
    const val BASE = "http://testyourapp.online/namasteyapp/api/"
    const val SPOTIFY_PROFILE_URL = "https://api.spotify.com/v1/me"

    //    api end-point
    const val REGISTER = "register"
    const val VERIFY_OTP = "verify-otp"
    const val GET_COUNTRY = "countries-list"
    const val GET_VIDEO_LANGUAGE = "video-language-list"
    const val GET_INTEREST_LIST = "interests-list"
    const val SOCIAL_LOGIN = "social-login"
    const val REGISTER_GUEST = "register-guest"
    const val GET_USER_DETAIL = "user-detail"
    const val GET_USER_FULL_PROFILE = "get-full-profile"
    const val CREATE_PROFILE = "profile"
    const val GET_CATEGORY_LIST = "category-list"
    const val UPDATE_PROFILE_PIC = "profile/media/add"
    const val GET_PROFILE_PIC = "profile/media/view"
    const val ADD_EDUCATION = "education/add"
    const val GET_EDUCATION = "education/view"
    const val REMOVE_EDUCATION = "education/remove"
    const val UPDATE_EDUCATION = "education/update"
    const val GET_JOB = "job-list"
    const val ADD_UPDATE_JOB = "add-job"
    const val REMOVE_JOB = "job-remove"
    const val ADD_SOCIAL_LINK = "social-links-add"
    const val GET_SOCIAL_LINK = "social-links-list"
    const val ADD_ALBUM = "add-album"
    const val GET_ALBUM = "album-list"
    const val GET_ALBUM_DETAILS = "album-details"
    const val GET_ALBUM_WITH_VIDEO = "album-with-video-details"
    const val POST_VIDEO = "post-video"
    const val POST_DELETE = "post-delete"
    const val ADD_MEDIA = "add-media"
    const val GET_FOLLOWER_LIST = "follower-list"
    const val GET_FOLLOWING_LIST = "following-list"
    const val GET_FEED_LIST = "feed-list"
    const val ADD_COMMENT = "add-comment"
    const val GET_COMMENT = "comment-list"
    const val DELETE_COMMENT = "comment-delete"
    const val USER_LIKE = "like"
    const val FOLLOW_REQUEST = "follow"
    const val REMOVE_FOLLOWERS = "remove-follow"
    const val SEARCH_USER = "search-user"
    const val GET_SUGGEST_LIST = "get-suggest-list"
    const val MULTIPLE_FOLLOW_REQUEST = "multiple-follow "
    const val REPORT_USER = "report-user"
    const val BLOCK_USER = "block-user"
    const val SAVE_POST = "save-post"
    const val GET_TREDING_VIDEOS = "treding-videos"
    const val POST_VIEWERS = "post-viewers"

    const val LOGIN = "login"
    const val LOGOUT = "logout"
    const val PASSWORD = "password"
    const val DEVICE_ID = "device_id"
    const val DEVICE_TYPE = "device_type"
    const val LATITUDE = "latitude"
    const val LONGITUDE = "longitude"
    const val PROFILE_BEAN = "profileBean"
    const val FROM_EDIT = "fromEdit"
    const val ALBUM_BEAN = "albumBean"
    const val VIDEO_LIST = "videoList"

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
    const val VIDEO_LANGUAGE_FRAGMENT = "VideoLanguageFragment"
    const val CHOOSE_INTEREST_FRAGMENT = "ChooseInterestFragment"
    const val COUNTRY_FRAGMENT = "CountryFragment"
    const val FIND_FRIEND_FRAGMENT = "FindFriendFragment"
    const val SELECT_FILTER_FRAGMENT = "SelectFilterFragment"
    const val ADD_FRIEND_FRAGMENT = "AddFriendFragment"
    const val SIGNUP_FRAGMENT = "SignUpFragment"
    const val SELECT_CATEGORY_FRAGMENT = "SelectCategoryFragment"
    const val INTEREST_IN_FRAGMENT = "InterestInFragment"
    const val EDUCATION_FRAGMENT = "EducationFragment"
    const val JOB_FRAGMENT = "JobFragment"
    const val ADD_LINKS_FRAGMENT = "AddLinksFragment"

    //    Session key
    const val KEY_USER_ID = "KEY_USER_ID"
    const val KEY_IS_LOGIN = "KEY_IS_LOGIN"
    const val KEY_SESSION_TOKEN = "KEY_SESSION_TOKEN"
    const val KEY_LOGIN_TYPE = "KEY_LOGIN_TYPE"
    const val KEY_INTERNET_AVAILABLE = "KEY_INTERNET_AVAILABLE"
    const val KEY_IS_VERIFIED_USER = "KEY_IS_VERIFIED_USER"
    const val KEY_GENDER = "KEY_GENDER"
    const val KEY_IS_GUEST_USER = "KEY_IS_GUEST_USER"
    const val KEY_CATEGORY_LIST = "KEY_CATEGORY_LIST"
    const val KEY_EDUCATION = "KEY_EDUCATION"
    const val KEY_JOB = "KEY_JOB"
    const val KEY_INTEREST_IN = "KEY_INTEREST_IN"
    const val KEY_CASUAL_NAME = "KEY_CASUAL_NAME"
    const val KEY_TAGLINE = "KEY_TAGLINE"
    const val KEY_AGE_MIN = "KEY_AGE_MIN"
    const val KEY_PROFILE_URL = "KEY_PROFILE_URL"
    const val KEY_AGE_MAX = "KEY_AGE_MAX"
    const val KEY_IS_COMPLETE_PROFILE = "KEY_IS_COMPLETE_PROFILE"


    //    field key
    const val ID = "id"
    const val MOBILE = "mobile"
    const val EMAIL = "email"
    const val IS_GUEST = "isGuest"
    const val USERNAME = "username"
    const val OTP = "otp"
    const val FACEBOOK = "facebook"
    const val SNAPCHAT = "snapchat"
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
    const val COLLEGE = "college"
    const val COURSE = "course"
    const val PASSING_YEAR = "passing_year"
    const val JOB_ID = "job_id"
    const val TITLE = "title"
    const val COMPANY_NAME = "company_name"
    const val NAME = "name"
    const val LINK = "link"
    const val ALBUM_ID = "album_id"
    const val DESCRIPTION = "description"
    const val SHARE_WITH = "share_with"
    const val IS_COMMENT = "is_comment"
    const val COVER_IMAGE = "cover_image"
    const val TAG_POST_VIDEO = "post_video"
    const val POST_VIDEO_ID = "post_video_id"
    const val FILE_TYPE = "file_type"
    const val MIN_AGE = "min_age"
    const val MAX_AGE = "max_age"
    const val TAG_LINE = "tagline_or_about_detail"
    const val TAGS = "tags"
    const val ALBUMS = "albums"
    const val SOCIAL_ACCOUNTS = "social_accounts"
    const val EDUCATION = "education"
    const val JOBS = "jobs"
    const val CATEGORY_ID = "category_id"
    const val POST_ID = "post_id"
    const val COMMENT = "comment"
    const val COMMENT_ID = "comment_id"
    const val LIKED_USER_ID = "liked_user_id"
    const val IS_LIKE = "is_like"
    const val USER_ID = "user_id"
    const val FOLLOWING_USER_ID = "following_user_id"
    const val IS_FOLLOWING = "is_following"
    const val FOLLOWERS_USER_ID = "follower_user_id"
    const val SEARCH = "search"
    const val REPORT_USER_ID = "report_user_id"
    const val REASON = "reason"
    const val BLOCK_USER_ID = "block_user_id"
    const val SUB_CAT_ID = "sub_cat_id"

    const val FILE = "file"
    const val IMAGE_TYPE = "image/*"
    const val VIDEO_TYPE = "video/*"
    const val TEXT_PLAIN = "text/plain"

    //    Other constants
    const val MIN_CHOOSE_INTEREST = 3
    const val DATE_FORMATE_DISPLAY = "dd/MM/yyyy"
    const val DATE_FORMATE_API = "yyyy-MM-dd"
    const val RC_SIGN_IN = 100
    const val REQUEST_VIDEO_SELECT = 101
    const val PERMISSION_CAMERA = 102
    const val REQUEST_POST_VIDEO = 103
    const val REQUEST_CODE_IMAGE = 104
    const val PERMISSION_STORAGE = 106
    const val FILTER_OK = 107
    const val ADD_LINK = 108
    const val REQUEST_CODE = 109
    const val REQUEST_CODE_EDUCATION = 110
    const val REQUEST_CODE_JOB = 111
    const val REQUEST_CODE_VIDEO_TRIM = 112
    const val REQUEST_SPOTIFY = 1337
    const val REQUEST_CODE_PROFILE = 113
    const val EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH"
    const val RESULT_CODE_PICK_THUMBNAIL = 114
    const val REQUEST_CODE_CAMERA_IMAGE = 115

    private val ROOT = File.separator + "NAMASTEY"
    private val SD_CARD_PATH = Environment.getExternalStorageDirectory().path
    val FILE_PATH = SD_CARD_PATH.plus(ROOT)
    val FILE_NAME = "temp.jpg"
    val FILE_NAME_VIDEO = "tempvideo.mp4"

}
