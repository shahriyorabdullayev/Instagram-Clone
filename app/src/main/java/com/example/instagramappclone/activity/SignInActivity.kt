package com.example.instagramappclone.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.instagramappclone.MainActivity
import com.example.instagramappclone.R
import com.example.instagramappclone.extensions.Extensions.toast
import com.example.instagramappclone.managers.handler.AuthHandler
import com.example.instagramappclone.managers.AuthManager
import com.example.instagramappclone.managers.DatabaseManager
import com.example.instagramappclone.managers.PrefsManager
import com.example.instagramappclone.managers.handler.DBUserHandler
import com.example.instagramappclone.model.User

/**
 * In SignUpActivity, user can login using email, password
 */

class SignInActivity : BaseActivity() {
    val TAG = SignInActivity::class.java.toString()
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        setupStatusBar()
        initViews()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)

        val btnSignIn = findViewById<Button>(R.id.btn_signin)
        btnSignIn.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                fireBaseSignIn(email, password)
            }
        }

        val tvSignUp = findViewById<TextView>(R.id.tv_signup)
        tvSignUp.setOnClickListener { callSignUpActivity() }
    }

    private fun fireBaseSignIn(email: String, password: String) {
        showLoading(this)
        AuthManager.signIn(email, password, object : AuthHandler {
            override fun onSuccess(uid: String) {
                dismissLoading()
                toast(getString(R.string.str_signin_success))
                storeDeviceTokenToUser()
            }

            override fun onError(exception: Exception?) {
                dismissLoading()
                toast(getString(R.string.str_signin_failed))

            }

        })
    }


    private fun storeDeviceTokenToUser() {
        val deviceToken = PrefsManager(this).loadDeviceToken()
        var uid = AuthManager.currentUser()!!.uid
        DatabaseManager.addMyDeviceToken(uid, deviceToken, object : DBUserHandler {
            override fun onSuccess(user: User?) {
                callMainActivity()
            }

            override fun onError(e: Exception) {
                callMainActivity()
            }

        })

    }


    private fun callSignUpActivity() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    private fun callMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}