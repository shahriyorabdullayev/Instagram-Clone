package com.example.instagramappclone.managers

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.support.annotation.ColorInt
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.instagramappclone.R
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class FirebaseConfig(var ll: LinearLayout, var tv: TextView) {

    var remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    init {
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        val configSettings = remoteConfigSettings{
            minimumFetchIntervalInSeconds = 3600
            fetchTimeoutInSeconds = 60
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
    }

    fun applyConfig(){

        val bg_color = remoteConfig.getString("main_background_color")
        val font_size = remoteConfig.getLong("welcome_font_size")
        val text = remoteConfig.getString("welcome_text")

        val gradient1 = remoteConfig.getString("gradient1")
        val gradient2 = remoteConfig.getString("gradient2")
        val tablet = remoteConfig.getString("tablet")

        Log.d("FirebaseConfig",  bg_color)
        Log.d("FirebaseConfig",  font_size.toString())
        Log.d("FirebaseConfig",  text)

        if (isTablet(ll.context)) {
            this.ll.setBackgroundColor(Color.parseColor(tablet))
        }else {
            this.ll.backgroundGradientDrawable(Color.parseColor(gradient1),Color.parseColor(gradient2))

        }
        this.tv.text = text
        this.tv.textSize = font_size.toFloat()

    }

    fun updateConfig(){
        remoteConfig.fetch(0).addOnCompleteListener {
            if (it.isSuccessful){
                Log.d("@@@", "updateConfig: ")
                remoteConfig.activate()
                applyConfig()
            }else {
                Log.d("TAG", "updateConfig: Fetch failed")
            }
        }
    }

    fun isTablet(context: Context): Boolean {
        val xlarge = context.resources
            .configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === 4
        val large = context.resources
            .configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === Configuration.SCREENLAYOUT_SIZE_LARGE
        return xlarge || large
    }


    fun View.backgroundGradientDrawable(@ColorInt startColor: Int, @ColorInt endColor: Int): Unit {
        val h = this.height.toFloat()
        val shapeDrawable = ShapeDrawable(RectShape())
        shapeDrawable.paint.shader =
            LinearGradient(0f, 0f, 0f, h, startColor, endColor, Shader.TileMode.REPEAT)
        this.background = shapeDrawable
    }

}