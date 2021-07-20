package com.namastey.uiView

import com.namastey.model.BlockUserListBean
import com.namastey.roomDB.entity.User
import java.util.*

interface AddVideoView : BaseView {
    fun onSuccessCreateOrUpdate(user: User)

}