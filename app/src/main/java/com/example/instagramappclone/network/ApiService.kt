package com.example.instagramappclone.network

import com.example.instagramappclone.model.FirebaseRequest
import com.example.instagramappclone.model.FirebaseResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

@JvmSuppressWildcards
interface ApiService {


    @Headers("Authorization:$ACCESS_KEY")
    @POST("send")
    fun sendNotification(@Body firebaseRequest: FirebaseRequest): Call<FirebaseResponse>

    companion object {
        const val ACCESS_KEY = "key=AAAAlu4b-7E:APA91bHZIm9Icj8A8kbk5SmXbFHdngCpcGCcoicOq-z05R8JVhsm8kXfhkOY3N_8zUWnyRhsWQ-BKGj4_ZpvifS84iiNaNc8JPyOkKGJAQBh-RC9USj2KsEcr1foeeL38HARDCU_zk-3"
    }
}