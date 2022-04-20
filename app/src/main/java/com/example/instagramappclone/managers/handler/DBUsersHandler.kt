package com.example.instagramappclone.managers.handler

import com.example.instagramappclone.model.User

interface DBUsersHandler {
    fun onSuccess(users: ArrayList<User>)
    fun onError(e: Exception)
}