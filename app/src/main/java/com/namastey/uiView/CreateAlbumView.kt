package com.namastey.uiView

import com.namastey.model.AlbumBean
import com.namastey.model.ProfileBean

interface CreateAlbumView : BaseView {
    fun onSuccessResponse(albumBean: AlbumBean)
//    fun onSuccessCreateProfile(profileBean: ProfileBean)
    fun onSuccessAlbumDetails(albumBeanList: ArrayList<AlbumBean>)
    fun onSuccessDeletePost()
}