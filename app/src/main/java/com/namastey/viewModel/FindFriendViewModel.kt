package com.namastey.viewModel

import com.namastey.R
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.FindFriendView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FindFriendViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var findFriendView: FindFriendView = baseView as FindFriendView
    private lateinit var job: Job

    fun getSearchUser(searchStr: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToSearchUser(searchStr).let { appResponse ->
                    setIsLoading(false)
                    if (appResponse.status == Constants.OK)
                        findFriendView.onSuccessSearchList(appResponse.data!!)
                    else
                        findFriendView.onFailed(appResponse.message, appResponse.error)
                }

            } catch (t: Throwable) {
                setIsLoading(false)
                findFriendView.onHandleException(t)
            }
        }

    }

    fun getSuggestedUser() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToGetSuggestList().let { appResponse ->
                    setIsLoading(false)
                    if (appResponse.status == Constants.OK)
                        findFriendView.onSuccessSuggestedList(appResponse.data!!)
                    else
                        findFriendView.onFailed(appResponse.message, appResponse.error)
                }

            } catch (t: Throwable) {
                setIsLoading(false)
                findFriendView.onHandleException(t)
            }
        }

    }

    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }

    fun sendMultipleFollow(selectUserIdList: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (findFriendView.isInternetAvailable()) {
                    setIsLoading(false)
                    networkService.requestToFollowMultipleUser(selectUserIdList, 1)
                        .let { appResponse ->
                            if (appResponse.status == Constants.OK)
                                findFriendView.onSuccess(appResponse.message)
                            else
                                findFriendView.onFailed(appResponse.message, appResponse.error)
                        }
                } else {
                    setIsLoading(false)
                    findFriendView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                findFriendView.onHandleException(t)
            }
        }

    }
}