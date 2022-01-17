package com.ferit.matijam.chatio.models


data class ChatMessage(
    var id:String?=null,
    var text:String?=null,
    var imageUrl:String?=null,
    var fromUserId: String?,
    var toUserId:String?=null,
    var timestamp: Long?=null,
    val localTime:String?=null

){
    constructor():this("","","","","",-1, "")

}
