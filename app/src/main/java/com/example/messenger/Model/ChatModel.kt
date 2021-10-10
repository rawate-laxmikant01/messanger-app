package com.example.messenger.Model

data class ChatModel(
    var msg: String? = "",
    var time: String? = "",
    var id: String? = "",
    var dateorder:String?="",
    var msgtype:Int=0,
    var documentid:String?=""
)