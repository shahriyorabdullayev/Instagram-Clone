package com.example.instagramappclone.activity
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.instagramappclone.R
import com.example.instagramappclone.managers.AuthManager
import com.example.instagramappclone.managers.PrefsManager
import com.example.instagramappclone.utils.Logger
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.messaging.FirebaseMessaging

/**
 * In SplashActivity, user can visit SignInActivity or MainActivity
 */

class SplashActivity : BaseActivity() {
    val TAG = SplashActivity::class.java.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        initViews()

    }


    private fun initViews() {
        countDownTimer()
        loadFCMToken()
    }

    private fun countDownTimer() {
        object : CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                if (AuthManager.isSignedIn()){
                    callMainActivity(this@SplashActivity)
                }else {
                    callSignInActivity(this@SplashActivity)
                }

            }

        }.start()
    }

    private fun loadFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Logger.d(TAG, "Fetching FCM registration token failed")
                return@OnCompleteListener
            }
            // Get new FCM registration token
            // Save it in locally to use later
            val token = task.result
            Logger.d(TAG, token.toString())
            PrefsManager(this).storeDeviceToken(token.toString())
        })
    }


}