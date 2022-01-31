package com.namastey.listeners

import android.media.MediaPlayer

interface OnChatMessageClick {
    fun onChatMessageClick(message: String, position: Int)
    fun onChatImageClick(imageUrl: String)
    fun onItemViewClick(mediaPlayer: MediaPlayer)
}