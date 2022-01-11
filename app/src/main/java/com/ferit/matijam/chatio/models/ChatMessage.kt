package com.ferit.matijam.chatio.models

import java.sql.Timestamp

data class ChatMessage(
    var id:String?=null,
    var text:String?=null,
    var imageUrl:String?=null,
    var fromUserId: String?,
    var toUserId:String?=null,
    var timestamp: Long?=null

){
    constructor():this("","","","","",-1)
}
