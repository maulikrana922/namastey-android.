package com.namastey.uiView

import com.namastey.model.VideoLanguageBean
import java.util.*

interface VideoLanguageView: BaseView{

    fun onClose()

    fun onNext()

    fun onSuccess(languageList: ArrayList<VideoLanguageBean>)

}