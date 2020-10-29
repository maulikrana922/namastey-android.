package com.namastey.viewModel

import com.namastey.model.AppResponse
import com.namastey.model.BlockUserListBean
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.BlockListView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BlockListViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var blockListView: BlockListView = baseView as BlockListView
    private lateinit var job: Job

    fun getBlockUserList() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToBlockUserList()
                    .let { appResponse: AppResponse<ArrayList<BlockUserListBean>> ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK) {
                            blockListView.onSuccessBlockUserList(appResponse.data!!)
                        } else {
                            blockListView.onFailed(appResponse.message, appResponse.error)
                        }
                    }
            } catch (exception: Throwable) {
                setIsLoading(false)
                blockListView.onHandleException(exception)
            }
        }
    }


    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}