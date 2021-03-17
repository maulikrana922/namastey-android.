package com.namastey.utils

import android.os.Environment
import java.io.File

object Constants {
    enum class Gender {
        male, female, other
    }

    object TwitterConstants {
        var CONSUMER_KEY = "66lJfE8259OsFIkRtSdZhXRid"
        var CONSUMER_SECRET = "fY7rgML5bVkY8X9ZNdnZtNIr6CpRfjmn2QTiRdsBeSAhUOZyau"
        var CALLBACK_URL = "twittersdk://"
    }

    object FirebaseConstant {
        var MESSAGES = "Messages"
        var CHATS = "Chats"
        var LAST_MESSAGE = "Last_message"
        var IMAGES = "Images"
        var VOICE = "Voice"
        var MSG_TYPE_IMAGE = "MsgTypeImage"
        var MSG_TYPE_VOICE = "MsgTypeVoice"
    }

    object InAppPurchaseConstants {
        var PACKAGE_NAME = "package_name"
        var PRODUCT_ID = "product_id"
        var PURCHASE_TOKEN = "purchase_token"
        var TITLE = "title"
        var DESCRIPTION = "description"
        var SUBSCRIPTION_PERIOD = "subscriptionPeriod"
        var PURCHASE_TIME = "purchaseTime"
        var PRICE = "price"
        var PRICE_CURRENCY_CODE = "price_currency_code"
        var PURCHASE_STATE = "purchaseState"
        var PURCHASE_TYPE = "purchase_type"
        var NO_OF_BOOST = "no_of_boost"
    }

    const val API_KEY = "Authorization"
    const val HVALUE = "L@titude2018"

    //    Live url
    const val BASE = "https://testyourapp.online/namasteyapp/api/"

    //  const val BASE = "http://198.74.55.170/namasteyapp/api/"
    const val SPOTIFY_PROFILE_URL = "https://api.spotify.com/v1/me"

    //    api end-point
    const val REGISTER = "register"
    const val VERIFY_OTP = "verify-otp"
    const val GET_COUNTRY = "countries-list"
    const val GET_VIDEO_LANGUAGE = "video-language-list"
    const val ALL_SUBCATEGORY_LIST = "all-subcategory-list"
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
    const val DELETE_ACCOUNT = "delete-account"
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
    const val FOLLOW = "follow"
    const val REMOVE_FOLLOWERS = "remove-follow"
    const val SEARCH_USER = "search-user"
    const val GET_SUGGEST_LIST = "get-suggest-list"
    const val MULTIPLE_FOLLOW_REQUEST = "multiple-follow "
    const val REPORT_USER = "report-user"
    const val BLOCK_USER = "block-user"
    const val SAVE_POST = "save-post"
    const val GET_TREDING_VIDEOS = "treding-videos"
    const val POST_VIEWERS = "post-viewers"
    const val HIDE_PROFILE = "hide-profile"
    const val USER_TYPE_CHANGE = "user-type-change"
    const val MENTION_LIST = "mentions-list"
    const val MATCHES_DELETE = "matches-delete"
    const val IS_VIDEO_DOWNLOAD = "is-video-download"
    const val WHO_CAN_SEE_YOUR_FOLLOWERS = "who-can-see-your-followers"
    const val ACTIVITY_LIST = "activity-list"
    const val SUGGEST_YOUR_ACCOUNT_ON_OFF = "suggest-your-account-on-of"
    const val WHO_CAN_COMMENT_YOUR_VIDEO = "who-can-comment-your-video"
    const val WHO_CAN_SEND_YOU_DIRECT_MESSAGE = "who-can-send-you-direct-message"
    const val SHARE_PROFILE_SAFETY = "share-profile-safety"
    const val MATCHES_READ = "matches-read"
    const val MESSAGE_CHAT_START = "message-chat-start"
    const val MESSAGE_USER_LIST = "message-user-list"
    const val DELETE_MESSAGE_CONVERSATION = "delete-message-conversation"
    const val DELETE_ALBUM = "delete-album"
    const val HIDE_ALBUM = "hide-album"
    const val GET_NEW_FEED_LIST = "new-feed-list"
    const val COMPARE_USERNAME = "compare-username"
    const val ADMIN_MESSAGE_LIST = "admin-message-list"
    const val PARTICULAR_CHAT_NOTIFICATION = "perticular-chat-notification"
    const val GET_PURCHASE_STATUS = "get-purchase-status"
    const val RECEIPT_VERIFICATION = "receipt-varification"
    const val BOOST_USE = "boost-use"
    const val FEED_LIST_V2 = "feed-list-v2"

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
    const val DISTANCE = "distance"
    const val MATCHES_LIST = "matches-list"
    const val FOLLOW_REQUEST_LIST = "follow-request-list"
    const val FOLLOW_REQUEST = "follow-request"
    const val BLOCK_USER_LIST = "block-user-list"
    const val NOTIFICATION_ON_OFF = "notification-on-of"
    const val MATCHES_LIST_BEAN = "matchesListBean"
    const val GET_POST_DETAILS = "get-post-details"
    const val BOOST_PRICE_LIST = "boost-price-list"
    const val MEMBERSHIP_PRICE_LIST = "membership-price-list"
    const val LIKED_USER_POST = "liked-user-post"
    const val LIKE_USER_POST = "like-user-post"
    const val LIKED_USER_COUNT = "liked-user-count"
    const val POST_SHARE = "post-share"
    const val ADD_USER_LOCATION = "add-user-location"
    const val ADD_USER_ACTIVE_TIME = "add-user-active-time"
    const val FOLLOWING_SHARE_LIST = "following-share-list"

    //    api response error code
    const val OK = 200
    const val ADMIN_BLOCK_USER_CODE = 404

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
    const val NOTIFICATION_FRAGMENT = "NotificationFragment"
    const val FOLLOW_REQUEST_FRAGMENT = "FollowRequestFragment"
    const val ACCOUNT_SETTINGS_FRAGMENT = "AccountSettingsFragment"
    const val ACCOUNT_SETTINGS_NOTIFICATIONS_FRAGMENT = "AccountSettingsNotificationFragment"
    const val BLOCK_LIST_FRAGMENT = "BlockListFragment"
    const val MANAGE_ACCOUNT_FRAGMENT = "ManageAccountFragment"
    const val CONTENT_LANGUAGE_FRAGMENT = "ContentLanguageFragment"
    const val SAFETY_FRAGMENT = "SafetyFragment"
    const val SAFETY_SUB_FRAGMENT = "SafetySubFragment"
    const val CHAT_SETTINGS_FRAGMENT = "ChatSettingsFragment"
    const val PERSONALIZE_DATA_FRAGMENT = "PersonalizeDataFragment"
    const val CURRENT_LOCATION_FRAGMENT = "CurrentLocationFragment"
    const val SHARE_APP_FRAGMENT = "ShareAppFragment"
    const val ADMIN_BLOCK_USER_FRAGMENt = "AdminBlockUserFragment"


    //    Session key
    const val KEY_USER_ID = "KEY_USER_ID"
    const val KEY_IS_LOGIN = "KEY_IS_LOGIN"
    const val KEY_SESSION_TOKEN = "KEY_SESSION_TOKEN"
    const val KEY_FIREBASE_TOKEN = "KEY_FIREBASE_TOKEN"
    const val KEY_LOGIN_TYPE = "KEY_LOGIN_TYPE"
    const val KEY_INTERNET_AVAILABLE = "KEY_INTERNET_AVAILABLE"
    const val KEY_IS_VERIFIED_USER = "KEY_IS_VERIFIED_USER"
    const val KEY_GENDER = "KEY_GENDER"
    const val KEY_IS_GUEST_USER = "KEY_IS_GUEST_USER"
    const val KEY_CATEGORY_LIST = "KEY_CATEGORY_LIST"
    const val KEY_EDUCATION = "KEY_EDUCATION"
    const val KEY_JOB = "KEY_JOB"
    const val KEY_INTEREST_IN = "KEY_INTEREST_IN"
    const val KEY_MAIN_USER_NAME = "KEY_MAIN_USER_NAME"
    const val KEY_TAGLINE = "KEY_TAGLINE"
    const val KEY_AGE_MIN = "KEY_AGE_MIN"
    const val KEY_PROFILE_URL = "KEY_PROFILE_URL"
    const val KEY_AGE_MAX = "KEY_AGE_MAX"
    const val KEY_IS_COMPLETE_PROFILE = "KEY_IS_COMPLETE_PROFILE"
    const val KEY_AGE = "KEY_AGE"
    const val KEY_IS_MATCHES = "KEY_IS_MATCHES"
    const val KEY_IS_COMMENTS = "KEY_IS_COMMENTS"
    const val KEY_IS_NEW_FOLLOWERS = "KEY_IS_NEW_FOLLOWERS"
    const val KEY_IS_MENTIONS = "KEY_IS_MENTIONS"
    const val KEY_IS_VIDEO_SUGGESTIONS = "KEY_IS_VIDEO_SUGGESTIONS"
    const val KEY_ALL_ACTIVITY = "KEY_ALL_ACTIVITY"
    const val KEY_ALL_ACTIVITY_TITLE = "KEY_ALL_ACTIVITY_TITLE"
    const val KEY_SUGGEST_YOUR_ACCOUNT_TO_OTHERS = "KEY_SUGGEST_YOUR_ACCOUNT_TO_OTHERS"
    const val KEY_IS_DOWNLOAD_VIDEO = "KEY_IS_DOWNLOAD_VIDEO"
    const val KEY_IS_SHARE_PROFILE_SAFETY = "KEY_IS_SHARE_PROFILE_SAFETY"
    const val KEY_IS_YOUR_FOLLOWERS = "KEY_IS_YOUR_FOLLOWERS"
    const val KEY_CAN_SEND_YOU_DIRECT_MESSAGE = "KEY_CAN_SEND_YOU_DIRECT_MESSAGE"
    const val KEY_CAN_COMMENT_YOUR_VIDEO = "KEY_CAN_COMMENT_YOUR_VIDEO"
    const val KEY_NOTIFICATION_DATA = "notification_data"
    const val KEY_LANGUAGE_LIST = "KEY_LANGUAGE_LIST"
    const val KEY_CHOOSE_INTEREST_LIST = "KEY_CHOOSE_INTEREST_LIST"
    const val KEY_MAX_USER_LIKE = "KEY_MAX_USER_LIKE"
    const val KEY_BOOST_ME = "KEY_BOOST_ME"
    const val KEY_SET_RECENT_LOCATION = "KEY_SET_RECENT_LOCATION"
    const val KEY_RECENT_LOCATION = "KEY_RECENT_LOCATION"
    const val KEY_SPEND_APP_TIME = "KEY_SPEND_APP_TIME"
    const val KEY_MUTE_PARTICULAR_USER = "KEY_MUTE_PARTICULAR_USER"
    const val KEY_BOOST_STAR_TIME = "KEY_BOOST_STAR_TIME"
    const val KEY_IS_PURCHASE = "KEY_IS_PURCHASE"
    const val KEY_NO_OF_BOOST = "KEY_NO_OF_BOOST"
    const val KEY_CASUAL_NAME = "KEY_CASUAL_NAME"


    //    field key
    const val ID = "id"
    const val MOBILE = "mobile"
    const val EMAIL = "email"
    const val IS_GUEST = "isGuest"
    const val USERNAME = "username"
    const val CASUAL_NAME = "casual_name"
    const val USER_UNIQUE_ID = "user_uniqueId"
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
    const val VIDEO_POST_ID = "video_post_id"
    const val COVER_IMAGE = "cover_image"
    const val VIDEO_URL = "video_url"
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
    const val IS_HIDE = "is_hide"
    const val FOLLOW_ID = "follow_id"
    const val PROFILE_TYPE = "profile_type"
    const val MATCH_USER_ID = "match_user_id"
    const val IS_READ = "is_read"
    const val MESSAGE_USER_ID = "message_user_id"
    const val IS_CHAT = "is_chat"
    const val PAGE = "page"
    const val LAT = "lat"
    const val LNG = "lng"

    const val FILE = "file"
    const val IMAGE_TYPE = "image/*"
    const val VIDEO_TYPE = "video/*"
    const val TEXT_PLAIN = "text/plain"
    const val IS_MENTIONS = "is_mentions"
    const val IS_MATCHES = "is_matches"
    const val IS_FOLLOW = "is_follow"
    const val IS_SUGGEST = "is_suggest"
    const val IS_BLOCK = "is_block"
    const val IS_DOWNLOAD = "is_download"
    const val IS_FOLLOWERS = "is_followers"
    const val IS_FILTER = "is_filter"
    const val WHO_CAN_COMMENT = "who_can_comment"
    const val WHO_CAN_SEND_DIRECT_MESSAGE = "who_can_send_message"
    const val IS_SHARE = "is_share"
    const val DEVICE_TOKEN = "device_token"
    const val USER_NAME = "user_name"
    const val ADDRESS = "address"
    const val TOTAL_TIME = "total_time"
    const val SENDER_ID = "sender_id"
    const val IS_NOTIFICATION = "is_notification"
    const val SUBSCRIPTION_TYPE = "subscriptionType"
    const val SUBSCRIPTION_ID = "subscriptionId"
    const val IN_APP_PRODUCT_ID = "inAppProductId"
    //const val POST_ID = "post_id"


    //    Other constants
    const val MIN_CHOOSE_INTEREST = 3
    const val DATE_FORMATE_DISPLAY = "dd/MM/yyyy"
    const val DATE_FORMATE_API = "yyyy-MM-dd"
    const val DATE_FORMATE_CHAT = "MMM d, hh:mm a"
    const val RC_SIGN_IN = 100
    const val REQUEST_VIDEO_SELECT = 101
    const val PERMISSION_CAMERA = 102
    const val REQUEST_POST_VIDEO = 103
    const val REQUEST_CODE_IMAGE = 104
    const val PERMISSION_STORAGE = 106
    const val PERMISSION_AUDIO = 116
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
    const val REQUEST_CODE_SLIDE_IMAGE = 117
    const val MAX_CHARACTER = 280

    private val ROOT = File.separator + "NAMASTEY"
    private val SD_CARD_PATH = Environment.getExternalStorageDirectory().path
    val FILE_PATH = SD_CARD_PATH.plus(ROOT)
    val FILE_NAME = "temp.jpg"
    val FILE_NAME_VIDEO = "tempvideo.mp4"

    //Push notification intent  keys
    const val ACTION_ACTION_TYPE = "action-type"
    const val NOTIFICATION_TYPE = "notification-type"
    const val NOTIFICATION_BROADCAST = "notification-broadcast"
    const val NOTIFICATION_PENDING_INTENT = "notification-pending-intent"
    const val KEY_NOTIFICATION = "notification"

}
