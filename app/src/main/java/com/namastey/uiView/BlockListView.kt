package com.namastey.uiView

import com.namastey.model.BlockUserListBean
import java.util.*

interface BlockListView : BaseView {
    fun onSuccessBlockUserList(data: ArrayList<BlockUserListBean>)
    fun onSuccessBlockUser(msg: String)

}