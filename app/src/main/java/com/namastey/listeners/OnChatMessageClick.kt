package com.namastey.listeners

interface OnChatMessageClick {
    fun onChatMessageClick(message: String, position: Int)
    fun onChatImageClick(imageUrl: String)
}