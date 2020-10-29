package com.namastey.uiView

import com.namastey.model.BlockUserListBean

interface BlockListView : BaseView {
    fun onSuccessBlockUserList(data: ArrayList<BlockUserListBean>)

}