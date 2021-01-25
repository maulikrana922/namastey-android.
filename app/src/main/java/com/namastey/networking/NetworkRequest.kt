package com.namastey.networking

import com.google.gson.JsonObject
import com.namastey.model.*
import com.namastey.roomDB.entity.Country
import com.namastey.roomDB.entity.User
import com.namastey.utils.Constants
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface NetworkRequest {

    @GET(Constants.GET_COUNTRY)
    fun requestToGetCountryAsync(): Deferred<AppResponse<ArrayList<Country>>>

    @POST(Constants.REGISTER)
    fun sendOTPAsync(@Body jsonObject: JsonObject): Deferred<AppResponse<User>>

    @FormUrlEncoded
    @POST(Constants.VERIFY_OTP)
    fun verifyOTPAsync(
        @Field(Constants.MOBILE) phone: String,
        @Field(Constants.EMAIL) email: String,
        @Field(Constants.OTP) otp: String,
        @Field(Constants.DEVICE_TOKEN) deviceToken: String
    ): Deferred<AppResponse<User>>

    @FormUrlEncoded
    @POST(Constants.GET_VIDEO_LANGUAGE)
    fun requestToGetVideoLanguageAsync(
        @Field(Constants.COUNTRY_CODE) local: String
    ): Deferred<AppResponse<ArrayList<VideoLanguageBean>>>

    @GET(Constants.GET_INTEREST_LIST)
    fun requestToGetChooseInterestAsync(): Deferred<AppResponse<ArrayList<InterestBean>>>

    @POST(Constants.SOCIAL_LOGIN)
    fun requestSocialLoginAsync(
        @Body jsonObject: JsonObject
    ): Deferred<AppResponse<User>>

    @POST(Constants.REGISTER_GUEST)
    fun requestCreateOrUpdateUserAsync(
        @Body jsonObject: JsonObject
    ): Deferred<AppResponse<User>>

    @GET(Constants.GET_USER_DETAIL)
    fun requestToGetUserDetailAsync(): Deferred<AppResponse<ProfileBean>>

    @FormUrlEncoded
    @POST(Constants.GET_USER_FULL_PROFILE)
    fun requestToGetUserFullProfileAsync(
        @Field(Constants.USER_ID) userId: String,
        @Field(Constants.USERNAME) username: String
    ): Deferred<AppResponse<ProfileBean>>

    @GET(Constants.GET_CATEGORY_LIST)
    fun requestToGetCategoryListAsync(): Deferred<AppResponse<ArrayList<CategoryBean>>>

    @Multipart
    @POST(Constants.UPDATE_PROFILE_PIC)
    fun requestUpdateProfilePicAsync(
        @Part file: MultipartBody.Part,
        @Part(Constants.DEVICE_TYPE) deviceType: RequestBody
    ): Deferred<AppResponse<Any>>

    @GET(Constants.GET_PROFILE_PIC)
    fun requestToGetProfilePicAsync(): Deferred<AppResponse<Any>>

    @GET(Constants.GET_EDUCATION)
    fun requestToGetEducationAsync(): Deferred<AppResponse<ArrayList<EducationBean>>>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = Constants.REMOVE_EDUCATION, hasBody = true)
    fun requestToRemoveEducationAsync(@Field(Constants.ID) id: Long): Deferred<AppResponse<Any>>

    @FormUrlEncoded
    @POST(Constants.ADD_EDUCATION)
    fun addEducationAsync(
        @Field(Constants.COLLEGE) college: String, @Field(Constants.COURSE) course: String
    ): Deferred<AppResponse<EducationBean>>

    @FormUrlEncoded
    @PUT(Constants.UPDATE_EDUCATION)
    fun updateEducationAsync(
        @Field(Constants.ID) id: String,
        @Field(Constants.COLLEGE) college: String,
        @Field(Constants.COURSE) course: String
    ): Deferred<AppResponse<EducationBean>>

    @GET(Constants.GET_JOB)
    fun requestToGetJobAsync(): Deferred<AppResponse<ArrayList<JobBean>>>

    @POST(Constants.ADD_UPDATE_JOB)
    fun addUpdateJobAsync(@Body jsonObject: JsonObject): Deferred<AppResponse<JobBean>>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = Constants.REMOVE_JOB, hasBody = true)
    fun requestToRemoveJobAsync(@Field(Constants.ID) id: Long): Deferred<AppResponse<Any>>

    @DELETE(Constants.DELETE_ACCOUNT)
    fun requestToRemoveAccount(): Deferred<AppResponse<Any>>

    @GET(Constants.LOGOUT)
    fun requestToLogout(): Deferred<AppResponse<Any>>

    @POST(Constants.ADD_SOCIAL_LINK)
    fun addSocialLinksAsync(@Body jsonObject: JsonObject): Deferred<AppResponse<ArrayList<SocialAccountBean>>>

    @GET(Constants.GET_SOCIAL_LINK)
    fun requestToGetSocialLinksAsync(): Deferred<AppResponse<ArrayList<SocialAccountBean>>>

    @POST(Constants.ADD_ALBUM)
    fun addAlbumAsync(@Body jsonObject: JsonObject): Deferred<AppResponse<AlbumBean>>

    @GET(Constants.GET_ALBUM)
    fun requestToGetAlbumListAsync(): Deferred<AppResponse<ArrayList<AlbumBean>>>

    @FormUrlEncoded
    @POST(Constants.GET_ALBUM_DETAILS)
    fun requestToGetAlbumDetailsAsync(@Field(Constants.ALBUM_ID) album_id: Long): Deferred<AppResponse<ArrayList<AlbumBean>>>

    @GET(Constants.GET_ALBUM_WITH_VIDEO)
    fun requestToGetAlbumWithDetailsAsync(): Deferred<AppResponse<ArrayList<AlbumBean>>>

    @POST(Constants.POST_VIDEO)
    fun postVideoAsync(
        @Body jsonObject: JsonObject
    ): Deferred<AppResponse<VideoBean>>

    @Multipart
    @POST(Constants.ADD_MEDIA)
    fun requestToAddMediaAsync(
        @Part post_video: MultipartBody.Part,
        @Part(Constants.FILE_TYPE) fileType: RequestBody,
        @Part(Constants.POST_VIDEO_ID) postVideoId: RequestBody,
        @Part(Constants.DEVICE_TYPE) deviceType: RequestBody
    ): Deferred<AppResponse<VideoBean>>

    @PUT(Constants.CREATE_PROFILE)
    fun requestCreateProfileAsync(@Body jsonObject: JsonObject): Deferred<AppResponse<Any>>

    @HTTP(method = "DELETE", path = Constants.POST_DELETE, hasBody = true)
    fun requestToDeletePostAsync(@Body jsonObject: JsonObject): Deferred<AppResponse<Any>>

    @FormUrlEncoded
    @POST(Constants.GET_FOLLOWER_LIST)
    fun requestToGetFollowerListAsync(@Field(Constants.USER_ID) userId: Long): Deferred<AppResponse<ArrayList<DashboardBean>>>

    @FormUrlEncoded
    @POST(Constants.GET_FOLLOWING_LIST)
    fun requestToGetFollowingListAsync(@Field(Constants.USER_ID) userId: Long): Deferred<AppResponse<ArrayList<DashboardBean>>>

    @FormUrlEncoded
    @POST(Constants.GET_FEED_LIST)
    fun requestToGetFeedListAsync(@Field(Constants.SUB_CAT_ID) subCatId: Int): Deferred<AppResponse<ArrayList<DashboardBean>>>

    @FormUrlEncoded
    @POST(Constants.ADD_COMMENT)
    fun requestToAddCommentAsync(
        @Field(Constants.POST_VIDEO_ID) postId: Long,
        @Field(Constants.COMMENT) comment: String
    ): Deferred<AppResponse<CommentBean>>

    @FormUrlEncoded
    @POST(Constants.GET_COMMENT)
    fun requestToGetCommentListAsync(
        @Field(Constants.POST_VIDEO_ID) postId: Long
    ): Deferred<AppResponse<ArrayList<CommentBean>>>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = Constants.DELETE_COMMENT, hasBody = true)
    fun requestToDeleteCommentAsync(
        @Field(Constants.COMMENT_ID) commentId: Long
    ): Deferred<AppResponse<Any>>

    @FormUrlEncoded
    @POST(Constants.USER_LIKE)
    fun requestToLikeUserAsync(
        @Field(Constants.LIKED_USER_ID) likedUserId: Long,
        @Field(Constants.IS_LIKE) isLike: Int
    ): Deferred<AppResponse<DashboardBean>>

    @FormUrlEncoded
    @POST(Constants.FOLLOW)
    fun requestToFollowUserAsync(
        @Field(Constants.FOLLOWING_USER_ID) followingUserId: Long,
        @Field(Constants.IS_FOLLOWING) isFollow: Int
    ): Deferred<AppResponse<Any>>

    @FormUrlEncoded
    @POST(Constants.FOLLOW)
    fun requestToFollowUserProfileAsync(
        @Field(Constants.FOLLOWING_USER_ID) followingUserId: Long,
        @Field(Constants.IS_FOLLOWING) isFollow: Int
    ): Deferred<AppResponse<ProfileBean>>

    @FormUrlEncoded
    @POST(Constants.REMOVE_FOLLOWERS)
    fun requestToRemoveFollowUserAsync(
        @Field(Constants.FOLLOWERS_USER_ID) followersUserId: Long,
        @Field(Constants.IS_FOLLOWING) isFollow: Int
    ): Deferred<AppResponse<Any>>

    @FormUrlEncoded
    @POST(Constants.SEARCH_USER)
    fun requestToSearchUserAsync(
        @Field(Constants.SEARCH) search: String
    ): Deferred<AppResponse<ArrayList<DashboardBean>>>

    @GET(Constants.GET_SUGGEST_LIST)
    fun requestToGetSuggestListAsync(): Deferred<AppResponse<ArrayList<DashboardBean>>>

    @FormUrlEncoded
    @POST(Constants.MULTIPLE_FOLLOW_REQUEST)
    fun requestToFollowMultipleUserAsync(
        @Field(Constants.FOLLOWING_USER_ID) followingUserId: String,
        @Field(Constants.IS_FOLLOWING) isFollow: Int
    ): Deferred<AppResponse<Any>>

    @FormUrlEncoded
    @POST(Constants.REPORT_USER)
    fun requestToreportUserAsync(
        @Field(Constants.REPORT_USER_ID) reportUserId: Long,
        @Field(Constants.REASON) reason: String
    ): Deferred<AppResponse<Any>>

    @FormUrlEncoded
    @POST(Constants.BLOCK_USER)
    fun requestToBlockUserAsync(
        @Field(Constants.BLOCK_USER_ID) userId: Long,
        @Field(Constants.IS_BLOCK) isBlock: Int
    ): Deferred<AppResponse<Any>>

    @FormUrlEncoded
    @POST(Constants.SAVE_POST)
    fun requestToSavePostAsync(
        @Field(Constants.POST_ID) postId: Long
    ): Deferred<AppResponse<Any>>

    @GET     // Call Spotify base url
    fun requestToGetSpotifyLinkAsync(
        @Url baseUrl: String,
        @Header("Authorization") authHeader: String
    ): Deferred<AppResponseSpotify<SpotifyBean>>

    @GET(Constants.GET_TREDING_VIDEOS)
    fun requestToGetTredingListAsync(): Deferred<AppResponse<ArrayList<VideoBean>>>

    @FormUrlEncoded
    @POST(Constants.POST_VIEWERS)
    fun requestToPostViewAsync(
        @Field(Constants.POST_ID) postId: Long
    ): Deferred<AppResponse<Any>>

    @FormUrlEncoded
    @POST(Constants.HIDE_PROFILE)
    fun requestToPostHideProfileAsync(
        @Field(Constants.IS_HIDE) isHide: Int
    ): Deferred<AppResponse<Any>>

    @FormUrlEncoded
    @POST(Constants.USER_TYPE_CHANGE)
    fun requestToPostProfileTypeAsync(
        @Field(Constants.PROFILE_TYPE) isPrivate: Int
    ): Deferred<AppResponse<Any>>

    @GET(Constants.MATCHES_LIST)
    fun requestToGetMatchesListAsync(): Deferred<AppResponse<ArrayList<MatchesListBean>>>

    @GET(Constants.FOLLOW_REQUEST_LIST)
    fun requestToGetFollowRequestAsync(): Deferred<AppResponse<ArrayList<FollowRequestBean>>>

    @FormUrlEncoded
    @POST(Constants.FOLLOW_REQUEST)
    fun requestToFollowRequestAsync(
        @Field(Constants.FOLLOW_ID) followId: Long,
        @Field(Constants.IS_FOLLOWING) isFollow: Int
    ): Deferred<AppResponse<Any>>

    @GET(Constants.BLOCK_USER_LIST)
    fun requestToGetBlockListAsync(): Deferred<AppResponse<ArrayList<BlockUserListBean>>>

    @POST(Constants.NOTIFICATION_ON_OFF)
    fun requestToNotificationOnOffAsync(@Body jsonObject: JsonObject): Deferred<AppResponse<NotificationOnOffBean>>

    @FormUrlEncoded
    @POST(Constants.MENTION_LIST)
    fun requestToPostMentionListAsync(
        @Field(Constants.SEARCH) search: String
    ): Deferred<AppResponse<ArrayList<MentionListBean>>>

    @FormUrlEncoded
    @POST(Constants.MATCHES_DELETE)
    fun requestToDeleteMatchesAsync(
        @Field(Constants.USER_ID) userId: Long
    ): Deferred<AppResponse<Any>>

    @FormUrlEncoded
    @POST(Constants.DELETE_MESSAGE_CONVERSATION)
    fun requestToDeleteChatAsync(
        @Field(Constants.USER_ID) userId: Long
    ): Deferred<AppResponse<Any>>

    @FormUrlEncoded
    @POST(Constants.IS_VIDEO_DOWNLOAD)
    fun requestToDownloadVideoAsync(
        @Field(Constants.IS_DOWNLOAD) isDownload: Int
    ): Deferred<AppResponse<SafetyBean>>

    @FormUrlEncoded
    @POST(Constants.WHO_CAN_SEE_YOUR_FOLLOWERS)
    fun requestToSeeYourFollowersAsync(
        @Field(Constants.IS_FOLLOWERS) isFollowers: Int
    ): Deferred<AppResponse<SafetyBean>>

    @FormUrlEncoded
    @POST(Constants.ACTIVITY_LIST)
    fun requestToPostActivityListAsync(
        @Field(Constants.IS_FILTER) isFilter: Int
    ): Deferred<AppResponse<ArrayList<ActivityListBean>>>

    @FormUrlEncoded
    @POST(Constants.SUGGEST_YOUR_ACCOUNT_ON_OFF)
    fun requestToSuggestYourAccountOnOffAsync(
        @Field(Constants.IS_SUGGEST) isSuggest: Int
    ): Deferred<AppResponse<SafetyBean>>

    @FormUrlEncoded
    @POST(Constants.WHO_CAN_COMMENT_YOUR_VIDEO)
    fun requestToWhoCanCommentYourVideoAsync(
        @Field(Constants.WHO_CAN_COMMENT) whoCanComment: Int
    ): Deferred<AppResponse<SafetyBean>>

    @FormUrlEncoded
    @POST(Constants.WHO_CAN_SEND_YOU_DIRECT_MESSAGE)
    fun requestToWhoCanSendYouDirectMessageAsync(
        @Field(Constants.WHO_CAN_SEND_DIRECT_MESSAGE) whoCanSendDirectMessage: Int
    ): Deferred<AppResponse<SafetyBean>>

    @FormUrlEncoded
    @POST(Constants.SHARE_PROFILE_SAFETY)
    fun requestToShareProfileSafetyAsync(
        @Field(Constants.IS_SHARE) isShare: Int
    ): Deferred<AppResponse<SafetyBean>>

    @GET(Constants.ALL_SUBCATEGORY_LIST)
    fun requestToGetAllSubCategoryListAsync(): Deferred<AppResponse<ArrayList<InterestSubCategoryBean>>>

    @FormUrlEncoded
    @POST(Constants.MATCHES_READ)
    fun requestToReadMatchesAsync(
        @Field(Constants.MATCH_USER_ID) matchesUserId: Long,
        @Field(Constants.IS_READ) isRead: Int
    ): Deferred<AppResponse<Any>>

    @FormUrlEncoded
    @POST(Constants.MESSAGE_CHAT_START)
    fun requestToStartChatAsync(
        @Field(Constants.MESSAGE_USER_ID) messageUserId: Long,
        @Field(Constants.IS_CHAT) isChat: Int
    ): Deferred<AppResponse<Any>>

    @GET(Constants.MESSAGE_USER_LIST)
    fun requestToGetChatMessageListAsync(): Deferred<AppResponse<ArrayList<MatchesListBean>>>

    @FormUrlEncoded
    @POST(Constants.GET_POST_DETAILS)
    fun requestToGetPostDetailsAsync(
        @Field(Constants.POST_ID) postId: Long
    ): Deferred<AppResponse<VideoBean>>

    @GET(Constants.BOOST_PRICE_LIST)
    fun requestToGetBoostPriceAsync(): Deferred<AppResponse<ArrayList<BoostPriceBean>>>

    @GET(Constants.MEMBERSHIP_PRICE_LIST)
    fun requestToGetMembershipPriceAsync(): Deferred<AppResponse<ArrayList<MembershipPriceBean>>>

    @GET(Constants.LIKED_USER_POST)
    fun requestToGetLikedUserPostAsync(): Deferred<AppResponse<ArrayList<VideoBean>>>

    @GET(Constants.LIKE_USER_POST)
    fun requestToGetLikeUserPostAsync(): Deferred<AppResponse<ArrayList<VideoBean>>>

    @GET(Constants.LIKED_USER_COUNT)
    fun requestToGetLikedUserCountAsync(): Deferred<AppResponse<LikedUserCountBean>>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = Constants.DELETE_ALBUM, hasBody = true)
    fun requestToDeleteAlbumAsync(
        @Field(Constants.ALBUM_ID) albumId: Long
    ): Deferred<AppResponse<Any>>

    @FormUrlEncoded
    @POST(Constants.HIDE_ALBUM)
    fun requestToHideAlbumAsync(
        @Field(Constants.ALBUM_ID) albumId: Long,
        @Field("is_hide") isHide: Int
    ): Deferred<AppResponse<Any>>

    @FormUrlEncoded
    @POST(Constants.POST_SHARE)
    fun requestToSharePostAsync(
        @Field(Constants.POST_ID) postId: Int,
        @Field(Constants.IS_SHARE) isShare: Int
    ): Deferred<AppResponse<Any>>

    @FormUrlEncoded
    @POST(Constants.GET_NEW_FEED_LIST)
    fun requestToGetNewFeedListAsync(
        @Field(Constants.PAGE) page: Int,
        @Field(Constants.SUB_CAT_ID) subCatId: Int,
        @Field(Constants.LAT) lat: Double,
        @Field(Constants.LNG) lng: Double
    ): Deferred<AppResponse<ArrayList<DashboardBean>>>

    @FormUrlEncoded
    @POST(Constants.COMPARE_USERNAME)
    fun requestToCheckUniqueUsernameAsync(
        @Field(Constants.USER_NAME) username: String
    ): Deferred<ErrorAppResponse<ErrorBean>>

    @FormUrlEncoded
    @POST(Constants.ADD_USER_LOCATION)
    fun requestToAddUserLocationAsync(
        @Field(Constants.ADDRESS) address: String,
        @Field(Constants.LAT) lat: String,
        @Field(Constants.LNG) lng: String
    ): Deferred<AppResponse<Any>>

    @FormUrlEncoded
    @POST(Constants.ADD_USER_ACTIVE_TIME)
    fun requestToAddUserActiveTimeAsync(
        @Field(Constants.TOTAL_TIME) totalTime: String
    ): Deferred<AppResponse<Any>>

}
