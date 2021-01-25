package com.namastey.networking

import com.google.gson.JsonObject
import com.namastey.model.*
import com.namastey.roomDB.entity.Country
import com.namastey.roomDB.entity.User
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class NetworkService(private val networkRequest: NetworkRequest) {

    /*If you want to pass params as json request then use below
    suspend fun requestLogin(loginRequest: LoginRequest): AppResponse<Login> = withContext(Dispatchers.Default) {
    networkRequest.requestLogin(loginRequest).await()
    }
    */

    suspend fun requestToGetCountry(): AppResponse<ArrayList<Country>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetCountryAsync().await()
        }

    suspend fun requestSendOTP(jsonObject: JsonObject): AppResponse<User> =
        withContext(Dispatchers.IO) { networkRequest.sendOTPAsync(jsonObject).await() }

    suspend fun requestVerifyOTP(
        phone: String,
        email: String,
        otp: String,
        deviceToken: String
    ): AppResponse<User> =
        withContext(Dispatchers.IO) {
            networkRequest.verifyOTPAsync(phone, email, otp, deviceToken).await()
        }

    suspend fun requestToGetVideoLanguage(locale: String): AppResponse<ArrayList<VideoLanguageBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetVideoLanguageAsync(locale).await()
        }

    suspend fun requestToGetChooseInterest(): AppResponse<ArrayList<InterestBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetChooseInterestAsync().await()
        }

    suspend fun requestSocialLogin(
        jsonObject: JsonObject
    ): AppResponse<User> = withContext(Dispatchers.IO) {
        networkRequest.requestSocialLoginAsync(
            jsonObject
        ).await()
    }

    suspend fun requestToUpdateOrCreateUser(
        jsonObject: JsonObject
    ): AppResponse<User> = withContext(Dispatchers.IO) {
        networkRequest.requestCreateOrUpdateUserAsync(
            jsonObject
        ).await()
    }

    suspend fun requestToGetUserDetail(): AppResponse<ProfileBean> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetUserDetailAsync().await()
        }

    suspend fun requestToGetUserFullProfile(
        userId: String,
        username: String
    ): AppResponse<ProfileBean> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetUserFullProfileAsync(userId, username).await()
        }

    suspend fun requestToGetCategoryList(): AppResponse<ArrayList<CategoryBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetCategoryListAsync().await()
        }

    suspend fun requestToGetFeed(subCatId: Int): AppResponse<ArrayList<DashboardBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetFeedListAsync(subCatId).await()
        }

    suspend fun requestUpdateProfilePicAsync(
        profile_image: MultipartBody.Part,
        deviceType: RequestBody
    ): AppResponse<Any> = withContext(Dispatchers.IO) {
        networkRequest.requestUpdateProfilePicAsync(
            profile_image,
            deviceType
        ).await()
    }

    suspend fun requestToGetProfilePic(): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetProfilePicAsync().await()
        }

    suspend fun requestToGetEducationList(): AppResponse<ArrayList<EducationBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetEducationAsync().await()
        }

    suspend fun requestToGetJobList(): AppResponse<ArrayList<JobBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetJobAsync().await()
        }

    suspend fun requestToRemoveJob(id: Long): AppResponse<Any> =
        withContext(Dispatchers.IO) { networkRequest.requestToRemoveJobAsync(id).await() }

    suspend fun requestToDeleteAccount(): AppResponse<Any> =
        withContext(Dispatchers.IO) { networkRequest.requestToRemoveAccount().await() }

    suspend fun requestToLogout(): AppResponse<Any> =
        withContext(Dispatchers.IO) { networkRequest.requestToLogout().await() }

    suspend fun requestAddEducation(college: String, year: String): AppResponse<EducationBean> =
        withContext(Dispatchers.IO) { networkRequest.addEducationAsync(college, year).await() }

    suspend fun requestUpdateEducation(
        id: String,
        college: String,
        year: String
    ): AppResponse<EducationBean> =
        withContext(Dispatchers.IO) {
            networkRequest.updateEducationAsync(id, college, year).await()
        }

    suspend fun requestToRemoveEducation(id: Long): AppResponse<Any> =
        withContext(Dispatchers.IO) { networkRequest.requestToRemoveEducationAsync(id).await() }

    suspend fun requestAddUpdateJob(jsonObject: JsonObject): AppResponse<JobBean> =
        withContext(Dispatchers.IO) { networkRequest.addUpdateJobAsync(jsonObject).await() }

    suspend fun requestAddSocialLinks(jsonObject: JsonObject): AppResponse<ArrayList<SocialAccountBean>> =
        withContext(Dispatchers.IO) { networkRequest.addSocialLinksAsync(jsonObject).await() }

    suspend fun requestToGetSocialLinksList(): AppResponse<ArrayList<SocialAccountBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetSocialLinksAsync().await()
        }

    suspend fun requestAddUpdateAlbum(jsonObject: JsonObject): AppResponse<AlbumBean> =
        withContext(Dispatchers.IO) { networkRequest.addAlbumAsync(jsonObject).await() }

    suspend fun requestToGetAlbumList(): AppResponse<ArrayList<AlbumBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetAlbumListAsync().await()
        }

    suspend fun requestToGetAlbumDetails(albumId: Long): AppResponse<ArrayList<AlbumBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetAlbumDetailsAsync(albumId).await()
        }

    suspend fun requestToGetAlbumWithDetails(): AppResponse<ArrayList<AlbumBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetAlbumWithDetailsAsync().await()
        }

    suspend fun requestPostVideo(
        jsonObject: JsonObject
    ): AppResponse<VideoBean> =
        withContext(Dispatchers.IO) {
            networkRequest.postVideoAsync(jsonObject).await()
        }

    suspend fun requestToAddMediaAsync(
        mbVideo: MultipartBody.Part,
        fileType: RequestBody,
        postVideoId: RequestBody,
        rbDeviceType: RequestBody
    ): AppResponse<VideoBean> = withContext(Dispatchers.IO) {
        networkRequest.requestToAddMediaAsync(
            mbVideo,
            fileType,
            postVideoId,
            rbDeviceType
        ).await()
    }

    suspend fun requestCreateProfile(jsonObject: JsonObject): AppResponse<Any> =
        withContext(Dispatchers.IO) { networkRequest.requestCreateProfileAsync(jsonObject).await() }

    suspend fun requestToDeletePost(jsonObject: JsonObject): AppResponse<Any> =
        withContext(Dispatchers.IO) { networkRequest.requestToDeletePostAsync(jsonObject).await() }

    suspend fun requestToGetFollowersList(userId: Long): AppResponse<ArrayList<DashboardBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetFollowerListAsync(userId).await()
        }

    suspend fun requestToGetFollowingList(userId: Long): AppResponse<ArrayList<DashboardBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetFollowingListAsync(userId).await()
        }

    suspend fun requestToAddComment(
        postId: Long, comment: String
    ): AppResponse<CommentBean> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToAddCommentAsync(
                postId, comment
            ).await()
        }

    suspend fun requestToGetCommentList(
        postId: Long
    ): AppResponse<ArrayList<CommentBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetCommentListAsync(
                postId
            ).await()
        }

    suspend fun requestToDeleteComment(
        commentId: Long
    ): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToDeleteCommentAsync(
                commentId
            ).await()
        }

    suspend fun requestToLikeUserProfile(
        likedUserId: Long,
        isLike: Int
    ): AppResponse<DashboardBean> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToLikeUserAsync(likedUserId, isLike).await()
        }

    suspend fun requestToFollowUser(followingUserId: Long, isFollow: Int): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToFollowUserAsync(followingUserId, isFollow).await()
        }

    // Temp set
    suspend fun requestToFollowUserProfile(
        followingUserId: Long,
        isFollow: Int
    ): AppResponse<ProfileBean> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToFollowUserProfileAsync(followingUserId, isFollow).await()
        }

    suspend fun requestToRemoveFollowUser(followersUserId: Long, isFollow: Int): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToRemoveFollowUserAsync(followersUserId, isFollow).await()
        }

    suspend fun requestToSearchUser(search: String): AppResponse<ArrayList<DashboardBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToSearchUserAsync(search).await()
        }

    suspend fun requestToGetSuggestList(): AppResponse<ArrayList<DashboardBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetSuggestListAsync().await()
        }

    suspend fun requestToFollowMultipleUser(
        selectUserIdList: String,
        isFollow: Int
    ): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToFollowMultipleUserAsync(selectUserIdList, isFollow).await()
        }

    suspend fun requestToReportUser(reportUserId: Long, reason: String): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToreportUserAsync(reportUserId, reason).await()
        }

    suspend fun requestToBlockUser(userId: Long, isBlock: Int): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToBlockUserAsync(userId, isBlock).await()
        }

    suspend fun requestToSavePost(postId: Long): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToSavePostAsync(postId).await()
        }

    suspend fun requestToGetSpotifyLink(token: String): AppResponseSpotify<SpotifyBean> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetSpotifyLinkAsync(Constants.SPOTIFY_PROFILE_URL, token)
                .await()
        }

    suspend fun requestToGetTredingVideo(): AppResponse<ArrayList<VideoBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetTredingListAsync().await()
        }

    suspend fun requestToPostView(postId: Long): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToPostViewAsync(postId).await()
        }

    suspend fun requestToHideProfile(isHide: Int): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToPostHideProfileAsync(isHide).await()
        }

    suspend fun requestToProfileType(isPrivate: Int): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToPostProfileTypeAsync(isPrivate).await()
        }

    suspend fun requestToGetMatchesList(): AppResponse<ArrayList<MatchesListBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetMatchesListAsync().await()
        }

    suspend fun requestToFollowRequest(): AppResponse<ArrayList<FollowRequestBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetFollowRequestAsync().await()
        }

    suspend fun requestToFollowAllowDenyRequest(followId: Long, isFollow: Int): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToFollowRequestAsync(followId, isFollow).await()
        }

    suspend fun requestToBlockUserList(): AppResponse<ArrayList<BlockUserListBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetBlockListAsync().await()
        }

    suspend fun requestToNotificationOnOff(jsonObject: JsonObject): AppResponse<NotificationOnOffBean> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToNotificationOnOffAsync(jsonObject).await()
        }

    suspend fun requestToPostMentionList(search: String): AppResponse<ArrayList<MentionListBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToPostMentionListAsync(search).await()
        }

    suspend fun requestToDeleteMatches(userId: Long): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToDeleteMatchesAsync(userId).await()
        }

    suspend fun requestToDeleteChat(userId: Long): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToDeleteChatAsync(userId).await()
        }

    suspend fun requestToDownloadVideo(isDownload: Int): AppResponse<SafetyBean> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToDownloadVideoAsync(isDownload).await()
        }

    suspend fun requestToSeeYourFollowers(isFollowers: Int): AppResponse<SafetyBean> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToSeeYourFollowersAsync(isFollowers).await()
        }

    suspend fun requestToPostActivityList(isFilter: Int): AppResponse<ArrayList<ActivityListBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToPostActivityListAsync(isFilter).await()
        }

    suspend fun requestToSuggestYourAccountOnOff(isSuggest: Int): AppResponse<SafetyBean> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToSuggestYourAccountOnOffAsync(isSuggest).await()
        }

    suspend fun requestToWhoCanCommentYourVideo(whoCanComment: Int): AppResponse<SafetyBean> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToWhoCanCommentYourVideoAsync(whoCanComment).await()
        }


    suspend fun requestToWhoCanSendYouDirectMessageVideo(whoCanSendDirectMessage: Int): AppResponse<SafetyBean> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToWhoCanSendYouDirectMessageAsync(whoCanSendDirectMessage).await()
        }

    suspend fun requestToShareProfileSafety(isShare: Int): AppResponse<SafetyBean> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToShareProfileSafetyAsync(isShare).await()
        }

    suspend fun requestToGetAllSubCategoryList(): AppResponse<ArrayList<InterestSubCategoryBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetAllSubCategoryListAsync().await()
        }

    suspend fun requestToReadMatches(matchesUserId: Long, isRead: Int): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToReadMatchesAsync(matchesUserId, isRead).await()
        }

    suspend fun requestToStartChat(messageUserId: Long, isChat: Int): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToStartChatAsync(messageUserId, isChat).await()
        }

    suspend fun requestToGetChatMessageList(): AppResponse<ArrayList<MatchesListBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetChatMessageListAsync().await()
        }

    suspend fun requestToGetPostDetails(postId: Long): AppResponse<VideoBean> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetPostDetailsAsync(postId).await()
        }

    suspend fun requestToGetBoostPriceList(): AppResponse<ArrayList<BoostPriceBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetBoostPriceAsync().await()
        }

    suspend fun requestToMembershipPriceList(): AppResponse<ArrayList<MembershipPriceBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetMembershipPriceAsync().await()
        }

    suspend fun requestToGetLikedUserPost(): AppResponse<ArrayList<VideoBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetLikedUserPostAsync().await()
        }

    suspend fun requestToGetLikeUserPost(): AppResponse<ArrayList<VideoBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetLikeUserPostAsync().await()
        }

    suspend fun requestToGetLikedUserCount(): AppResponse<LikedUserCountBean> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetLikedUserCountAsync().await()
        }

    suspend fun requestToDeleteAlbum(albumId: Long): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToDeleteAlbumAsync(albumId).await()
        }

    suspend fun requestToHideAlbum(albumId: Long, isHide: Int): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToHideAlbumAsync(albumId, isHide).await()
        }

    suspend fun requestToSharePost(postId: Int, isShare: Int): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToSharePostAsync(postId, isShare).await()
        }

    suspend fun requestToGetNewFeed(
        page: Int,
        subCatId: Int,
        lat: Double,
        lng: Double
    ): AppResponse<ArrayList<DashboardBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetNewFeedListAsync(page, subCatId, lat, lng).await()
        }

    suspend fun requestToCheckUniqueUsername(username: String): ErrorAppResponse<ErrorBean> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToCheckUniqueUsernameAsync(username).await()
        }

    suspend fun requestToAddUserLocation(
        address: String,
        lat: String,
        lng: String
    ): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToAddUserLocationAsync(address, lat, lng).await()
        }

    suspend fun requestToAddUserActiveTime(
        totalTime: String
    ): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToAddUserActiveTimeAsync(totalTime).await()
        }

}
