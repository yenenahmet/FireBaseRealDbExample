package com.spexco.firebaserealdbexample.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User (
    var userKey:String="",
    var userAge:Int =0,
    var userEmail:String ="",
    var userName:String  =""
)