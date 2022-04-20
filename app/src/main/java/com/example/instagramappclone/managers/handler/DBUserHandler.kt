package com.example.instagramappclone.managers.handler

import com.example.instagramappclone.model.User


interface DBUserHandler {
    fun onSuccess(user: User? = null)
    fun onError(e: Exception)

}