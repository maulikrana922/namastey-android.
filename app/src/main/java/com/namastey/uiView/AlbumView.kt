package com.namastey.uiView

import com.namastey.model.AlbumBean
import java.util.ArrayList

interface AlbumView: BaseView {

    fun onSuccessAlbumList(arrayList: ArrayList<AlbumBean>)

}