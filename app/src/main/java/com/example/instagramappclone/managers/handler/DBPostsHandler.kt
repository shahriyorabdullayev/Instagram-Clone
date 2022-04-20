package com.example.instagramappclone.managers.handler

import com.example.instagramappclone.model.Post

interface DBPostsHandler {
    fun onSuccess(posts: ArrayList<Post>)
    fun onError(e: Exception)
}