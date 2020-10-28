package com.namastey.uiView

import com.namastey.model.VideoLanguageBean

interface ContentLanguageView : BaseView {
    fun onSuccess(languageList: ArrayList<VideoLanguageBean>)
}