package com.example.instagramappclone.model

import com.example.instagramappclone.managers.PrefsManager

class User {
    var uid: String = ""
    var fullname: String = ""
    var email: String = ""
    var password: String = ""
    var userImg: String = ""

    var device_id = ""
    var device_type = "A"
    var device_tokens : ArrayList<String> = ArrayList()

    var isFollowed: Boolean = false

    constructor(fullname: String, email: String){
        this.fullname = fullname
        this.email = email
    }

    constructor(fullname: String, email: String, device_tokens: ArrayList<String>) {
        this.email = email
        this.fullname = fullname
        this.device_tokens = device_tokens
    }

    constructor(fullname: String, email: String, password: String, userImg: String) {
        this.email = email
        this.fullname = fullname
        this.userImg = userImg
        this.password = password
    }


}