package com.namastey.viewModel

import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.ChatBasicView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ChatViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var chatBasicView: ChatBasicView = baseView as ChatBasicView
    private lateinit var job: Job

    fun reportUser(reportUserId: Long, reason: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToReportUser(reportUserId, reason).let { appResponse ->
                    setIsLoading(false)
                    if (appResponse.status == Constants.OK)
                        chatBasicView.onSuccessReport(appResponse.message)
                    else
                        chatBasicView.onFailed(appResponse.message, appResponse.error)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                chatBasicView.onHandleException(t)
            }
        }
    }

    fun blockUser(userId: Long, isBlock: Int) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToBlockUser(userId, isBlock).let { appResponse ->
                    setIsLoading(false)
                    if (appResponse.status == Constants.OK)
                        chatBasicView.onSuccessBlockUser(appResponse.message)
                    else
                        chatBasicView.onFailed(appResponse.message, appResponse.error)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                chatBasicView.onHandleException(t)
            }
        }
    }

    fun deleteMatches(userId: Long) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToDeleteMatches(userId).let { appResponse ->
                    setIsLoading(false)
                    if (appResponse.status == Constants.OK)
                        chatBasicView.onSuccessDeleteMatches(appResponse.message)
                    else
                        chatBasicView.onFailed(appResponse.message, appResponse.error)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                chatBasicView.onHandleException(t)
            }
        }
    }

    fun readMatches(matchesUserId: Long, isRead: Int) {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToReadMatches(matchesUserId,isRead).let { appResponse ->
                    if (appResponse.status == Constants.OK)
                        chatBasicView.onSuccess(appResponse.message)
                    else
                        chatBasicView.onFailed(appResponse.message, appResponse.error)
                }
            } catch (t: Throwable) {
                chatBasicView.onHandleException(t)
            }
        }
    }

    fun onDestroy() {
        if (::job.isInitialized) {
            job.cancel()
        }
    }
}