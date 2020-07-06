package com.namastey.listeners

import com.namastey.model.AlbumBean

interface OnCreateAlbumItemClick {

    fun onCreateAlbumItemClick(
        albumBean: AlbumBean,
        position: Int,
        isCreate: Boolean
    )

    fun onClickAddVideo(albumBean: AlbumBean)

}