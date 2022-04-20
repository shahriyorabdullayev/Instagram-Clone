package com.example.instagramappclone.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClient {

    var BASE_URL = "https://fcm.googleapis.com/fcm/"



    fun getRetrofit(): Retrofit {
        val build = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
        return build
    }

    fun <T> createService(service: Class<T>): T {
        return getRetrofit().create(service)
    }

}