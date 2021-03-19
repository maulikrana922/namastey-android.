package com.namastey.uiView

import com.namastey.model.VideoLanguageBean
import java.util.*

interface ContentLanguageView : BaseView {
    fun onSuccess(languageList: ArrayList<VideoLanguageBean>)
}