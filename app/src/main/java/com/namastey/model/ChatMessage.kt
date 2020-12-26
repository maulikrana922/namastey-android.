package com.namastey.model

class ChatMessage(
        var message: String,
        var sender: Long,
        var receiver: Long,
        var url: String,
        var timestamp: Long
) {
    constructor() : this( "", -1, -1,"",-1)
}