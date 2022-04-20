package com.example.instagramappclone.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.instagramappclone.R
import com.example.instagramappclone.extensions.Extensions.toast
import com.example.instagramappclone.managers.handler.AuthHandler
import com.example.instagramappclone.managers.AuthManager
import com.example.instagramappclone.managers.handler.DBUserHandler
import com.example.instagramappclone.managers.DatabaseManager
import com.example.instagramappclone.managers.PrefsManager
import com.example.instagramappclone.model.User


/**
 * In SignUpActivity, user can signup using fullname, email, password
 */

class SignUpActivity : BaseActivity() {
    val TAG = SignUpActivity::class.java.toString()
    lateinit var etFullname: EditText
    lateinit var etPassword: EditText
    lateinit var etEmail: EditText
    lateinit var etConfirmPassword: EditText
    private lateinit var tvSignIn :TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setupStatusBar()
        initViews()


    }

    private fun initViews() {
        tvSignIn = findViewById(R.id.tv_signin)
        etFullname = findViewById(R.id.et_fullname)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)

        val btnSignUp = findViewById<Button>(R.id.btn_signup)
        btnSignUp.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val fullname = etFullname.text.toString().trim()
            val deviceToken = PrefsManager(this).loadDeviceToken()!!


            if (email.isNotEmpty() && password.isNotEmpty() && fullname.isNotEmpty()) {
                val user = User(fullname, email, password, "")
                user.device_tokens.add(deviceToken)
                firebaseSignUp(user)
            }else {
                toast("To'liq to'ldiring")
            }
        }

        tvSignIn.setOnClickListener {
            callSignUpActivity()
        }


    }

    private fun firebaseSignUp(user: User) {
        showLoading(this)
        AuthManager.signUp(user.email, user.password, object: AuthHandler {
            override fun onSuccess(uid: String) {
                user.uid = uid
                storeUserToDb(user)
                toast(getString(R.string.str_signup_success))
            }

            override fun onError(exception: Exception?) {
                dismissLoading()
                toast(getString(R.string.str_signup_failed))
            }

        })
    }

    private fun storeUserToDb(user: User) {
        DatabaseManager.storeUser(user, object : DBUserHandler {
            override fun onSuccess(user: User?) {
                dismissLoading()
                callMainActivity(this@SignUpActivity)
            }

            override fun onError(e: Exception) {

            }
        })

    }

    private fun callSignUpActivity() {
        startActivity(Intent(this, SignInActivity::class.java))
    }
}