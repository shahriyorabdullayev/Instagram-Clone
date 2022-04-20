package com.example.instagramappclone.managers.handler

import java.lang.Exception

interface StorageHandler {
    fun onSuccess(imgUrl: String)
    fun onError(exception: Exception?)
}