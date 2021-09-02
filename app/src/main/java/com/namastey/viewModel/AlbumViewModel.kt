package com.namastey.viewModel

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.namastey.R
import com.namastey.model.AppResponse
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.AlbumView
import com.namastey.uiView.BaseView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AlbumViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var albumView = baseView as AlbumView
    private lateinit var job: Job
    private val _downloading: MutableLiveData<Boolean> = MutableLiveData()

    fun setDownloading(downloading: Boolean) {
        _downloading.value = downloading
    }

    fun getAlbumList() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToGetAlbumList().let { appResponse ->
                    setIsLoading(false)
                    if (appResponse.status == Constants.OK)
                        albumView.onSuccessAlbumList(appResponse.data!!)
                    else
                        albumView.onFailed(appResponse.message, appResponse.error,
                            appResponse.status)
                }

            } catch (t: Throwable) {
                setIsLoading(false)
                albumView.onHandleException(t)
            }
        }

    }

    fun getCommentList(postId: Long) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (albumView.isInternetAvailable()) {
                    networkService.requestToGetCommentList(postId).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            albumView.onSuccessGetComment(appResponse.data!!)
                        else
                            albumView.onFailed(appResponse.message, appResponse.error,
                                appResponse.status)
                    }
                } else {
                    setIsLoading(false)
                    albumView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                albumView.onHandleException(t)
            }
        }
    }

    fun addComment(postId: Long, edtComment: String) {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (albumView.isInternetAvailable()) {
                    networkService.requestToAddComment(postId, edtComment).let { appResponse ->
                        if (appResponse.status == Constants.OK)
                            albumView.onSuccessAddComment(appResponse.data!!)
                        else
                            albumView.onFailed(appResponse.message, appResponse.error,
                                appResponse.status)
                    }
                } else {
                    albumView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                albumView.onHandleException(t)
            }
        }
    }

    fun deleteComment(commentId: Long) {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (albumView.isInternetAvailable()) {
                    networkService.requestToDeleteComment(commentId).let { appResponse ->
                        if (appResponse.status == Constants.OK)
                            albumView.onSuccess(appResponse.message)
                        else
                            albumView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                    }
                } else {
                    albumView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                albumView.onHandleException(t)
            }
        }
    }

    fun savePost(postId: Long) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (albumView.isInternetAvailable()) {
                    networkService.requestToSavePost(postId).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            albumView.onSuccessSavePost(appResponse.message)
                        else
                            albumView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                    }
                } else {
                    setIsLoading(false)
                    albumView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                albumView.onHandleException(t)
            }
        }

    }

    fun reportUser(reportUserId: Long, reason: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (albumView.isInternetAvailable()) {
                    networkService.requestToReportUser(reportUserId, reason).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            albumView.onSuccessSavePost(appResponse.message)
                        else
                            albumView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                    }
                } else {
                    setIsLoading(false)
                    albumView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                albumView.onHandleException(t)
            }
        }
    }

    fun blockUser(userId: Long, isBlock:Int) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (albumView.isInternetAvailable()) {
                    networkService.requestToBlockUser(userId, isBlock).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            albumView.onSuccessBlockUser(appResponse.message)
                        else
                            albumView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                    }
                } else {
                    setIsLoading(false)
                    albumView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                albumView.onHandleException(t)
            }
        }
    }

    fun postView(postId: Long) {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (albumView.isInternetAvailable()) {
                    networkService.requestToPostView(postId).let { appResponse ->

                    }
                } else {
                    albumView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                albumView.onHandleException(t)
            }
        }

    }

    fun likeUserProfile(likedUserId: Long, isLike: Int) {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (albumView.isInternetAvailable()) {
                    networkService.requestToLikeUserProfile(likedUserId, isLike)
                        .let { appResponse ->
                            if (appResponse.status == Constants.OK)
                                appResponse.data?.let { albumView.onSuccessProfileLike(appResponse.data!!) }
                            else
                                albumView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                        }
                } else {
                    albumView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                albumView.onHandleException(t)
            }
        }
    }

    fun postShare(postId: Int, isShare: Int) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (albumView.isInternetAvailable()) {
                    networkService.requestToSharePost(postId, isShare).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            albumView.onSuccessPostShare(appResponse.message)
                        else
                            albumView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                    }
                } else {
                    setIsLoading(false)
                    albumView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                albumView.onHandleException(t)
            }
        }
    }


    fun getFollowingShareList() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (albumView.isInternetAvailable()) {
                    networkService.requestToGetFollowingShareList().let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            albumView.onSuccess(appResponse.data!!)
                        else
                            albumView.onFailed(
                                appResponse.message,
                                appResponse.error,
                                appResponse.status
                            )
                    }
                } else {
                    setIsLoading(false)
                    albumView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                albumView.onHandleException(t)
            }
        }


    }

    fun startChat(messageUserId: Long, isChat: Int) {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToStartChat(messageUserId, isChat).let { appResponse ->
                    if (appResponse.status == Constants.OK)
                        albumView.onSuccessStartChat(appResponse.message)
//                    else
//                        chatBasicView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                }
            } catch (t: Throwable) {
//                chatBasicView.onHandleException(t)
            }
        }
    }

    fun removePostVideo(postId: Long, isSaved: Int) {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (albumView.isInternetAvailable()) {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty(Constants.POST_ID, postId)
                    jsonObject.addProperty(Constants.IS_SAVED, isSaved)
                    networkService.requestToDeletePost(jsonObject)
                        .let { appResponse: AppResponse<Any> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                albumView.onSuccessDeletePost()
                            } else {
                                albumView.onFailed(
                                    appResponse.message,
                                    appResponse.error,
                                    appResponse.status
                                )
                            }
                        }
                } else {
                    setIsLoading(false)
                    albumView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                albumView.onHandleException(exception)
            }
        }

    }

    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}