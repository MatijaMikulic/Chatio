package com.ferit.matijam.chatio.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(
    var uid:String?=null,
    var username:String?=null,
    var email:String?=null,
    var imageUrl:String?=null,
    var queryUsername:String?=null
):Parcelable

