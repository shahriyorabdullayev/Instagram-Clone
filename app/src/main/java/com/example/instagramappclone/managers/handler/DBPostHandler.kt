package com.example.instagramappclone.managers.handler

import com.example.instagramappclone.model.Post


interface DBPostHandler {
    fun onSuccess(post:Post)
    fun onError(e: Exception)
}