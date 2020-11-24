package com.namastey.model

class ChatMessage(
        val message: String,
        val sender: Long,
        val receiver: Long
) {
    constructor() : this( "", -1, -1)
}