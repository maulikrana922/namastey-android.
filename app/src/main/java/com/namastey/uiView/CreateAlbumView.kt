package com.namastey.uiView

import com.namastey.model.AlbumBean
import java.util.*

interface CreateAlbumView : BaseView {
    fun onSuccessResponse(albumBean: AlbumBean)
//    fun onSuccessCreateProfile(profileBean: ProfileBean)
    fun onSuccessAlbumDetails(albumBeanList: ArrayList<AlbumBean>)
    fun onSuccessDeletePost()
    fun onSuccessAlbumDelete(msg: String)
    fun onSuccessAlbumHide(msg: String)

}