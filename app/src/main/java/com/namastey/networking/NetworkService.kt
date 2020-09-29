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

    suspend fun requestVerifyOTP(phone: String, email: String, otp: String): AppResponse<User> =
        withContext(Dispatchers.IO) { networkRequest.verifyOTPAsync(phone, email, otp).await() }

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

    suspend fun requestToGetUserFullProfile(userId: Long): AppResponse<ProfileBean> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetUserFullProfileAsync(userId).await()
        }

    suspend fun requestToGetCategoryList(): AppResponse<ArrayList<CategoryBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetCategoryListAsync().await()
        }

    suspend fun requestToGetFeed(): AppResponse<ArrayList<DashboardBean>> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetFeedListAsync().await()
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
        description: String,
        album_id: Long,
        share_with: Int,
        is_comment: Int
    ): AppResponse<VideoBean> =
        withContext(Dispatchers.IO) {
            networkRequest.postVideoAsync(
                description,
                album_id,
                share_with,
                is_comment
            ).await()
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

    suspend fun requestToLikeUserProfile(likedUserId: Long, isLike: Int): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToLikeUserAsync(likedUserId, isLike).await()
        }

    suspend fun requestToFollowUser(followingUserId: Long, isFollow: Int): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToFollowUserAsync(followingUserId, isFollow).await()
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

    suspend fun requestToFollowMultipleUser(selectUserIdList: String, isFollow: Int): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToFollowMultipleUserAsync(selectUserIdList, isFollow).await()
        }

    suspend fun requestToReportUser(reportUserId: Long, reason: String): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToreportUserAsync(reportUserId,reason).await()
        }

    suspend fun requestToBlockUser(userId: Long): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToBlockUserAsync(userId).await()
        }

    suspend fun requestToSavePost(postId: Long): AppResponse<Any> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToSavePostAsync(postId).await()
        }

    suspend fun requestToGetSpotifyLink(token: String): AppResponseSpotify<SpotifyBean> =
        withContext(Dispatchers.IO) {
            networkRequest.requestToGetSpotifyLinkAsync(Constants.SPOTIFY_PROFILE_URL, token).await()
        }
}
