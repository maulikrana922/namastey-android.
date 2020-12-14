package com.namastey.uiView

import com.namastey.model.VideoBean
import java.util.*

interface ProfileLikeView : BaseView {
    fun onSuccess(data: ArrayList<VideoBean>)
}