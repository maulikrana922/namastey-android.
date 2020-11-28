package com.namastey.model

class ChatMessage(
        val message: String,
        val sender: Long,
        val receiver: Long,
        val url: String,
        val timestamp: Long
) {
    constructor() : this( "", -1, -1,"",-1)
}