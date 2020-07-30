package com.namastey.viewModel

import com.namastey.R
import com.namastey.model.AppResponse
import com.namastey.model.VideoBean
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.PostVideoView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class PostVideoViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var postVideoView = baseView as PostVideoView
    private lateinit var job: Job

    fun postVideoDesc(
        description: String,
        albumId: Long,
        shareWith: Int,
        commentOff: Int
    ) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (postVideoView.isInternetAvailable()) {
                    networkService.requestPostVideo(description,albumId,shareWith,commentOff)
                        .let { appResponse: AppResponse<VideoBean> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                postVideoView.onSuccessPostVideoDesc(appResponse.data!!)
                            } else {
                                postVideoView.onFailed(appResponse.message, appResponse.error)
                            }
                        }
                } else {
                    setIsLoading(false)
                    postVideoView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                postVideoView.onHandleException(exception)
            }
        }
    }

    fun addMedia(postVideoId: Long, videoFile: File?) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
//                file_type: post_video
//                file_type: cover_image
                    var mbVideo: MultipartBody.Part? = null
                    if (videoFile != null && videoFile.exists()) {
                        mbVideo = MultipartBody.Part.createFormData(
                            Constants.FILE,
                            videoFile.name,
                            RequestBody.create(MediaType.parse("*/*"), videoFile)
                        )
                    }
                    val rbDeviceType = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN), Constants.ANDROID)
                    val rbPostVideoId = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN), postVideoId.toString())
                    val rbFileType = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN), Constants.TAG_POST_VIDEO)

                    if (mbVideo != null) {
                        networkService.requestToAddMediaAsync(mbVideo,rbFileType,rbPostVideoId,rbDeviceType).let { appResponse ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK)
                                postVideoView.onSuccessPostVideo(appResponse.data!!)
                            else
                                postVideoView.onFailed(appResponse.message, appResponse.error)
                        }
                    }
            } catch (t: Throwable) {
                setIsLoading(false)
                postVideoView.onHandleException(t)
            }
        }

    }

    fun addMediaCoverImage(postVideoId: Long, pictuareFile: File?) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {

                var mbImage: MultipartBody.Part? = null
                if (pictuareFile != null && pictuareFile.exists()) {
                        mbImage = MultipartBody.Part.createFormData(
                            Constants.FILE,
                            pictuareFile.name,
                            RequestBody.create(MediaType.parse("*/*"), pictuareFile)
                        )
                    }

                    val rbDeviceType = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN), Constants.ANDROID)
                    val rbPostVideoId = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN), postVideoId.toString())
                    val rbFileType = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN), Constants.COVER_IMAGE)

                    if (mbImage != null) {
                        networkService.requestToAddMediaAsync(mbImage,rbFileType,rbPostVideoId,rbDeviceType).let { appResponse ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK)
                                postVideoView.onSuccessPostCoverImage(appResponse.data!!)
                            else
                                postVideoView.onFailed(appResponse.message, appResponse.error)
                        }
                    }
            } catch (t: Throwable) {
                setIsLoading(false)
                postVideoView.onHandleException(t)
            }
        }

    }

    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }

    fun getAlbumList() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                    networkService.requestToGetAlbumList().let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            postVideoView.onSuccessAlbumList(appResponse.data!!)
                        else
                            postVideoView.onFailed(appResponse.message,appResponse.error)
                    }

            } catch (t: Throwable) {
                setIsLoading(false)
                postVideoView.onHandleException(t)
            }
        }

    }
}