package com.namastey.uiView

import com.namastey.model.AlbumBean

interface CreateAlbumView: BaseView {
    fun onSuccessResponse(albumBean: AlbumBean)

}