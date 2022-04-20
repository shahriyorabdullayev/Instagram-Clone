package com.example.instagramappclone.managers.handler

interface DBFollowHandler {
    fun onSuccess(isFollowed: Boolean)
    fun onError(e: Exception)
}