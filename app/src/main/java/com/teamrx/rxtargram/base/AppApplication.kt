package com.teamrx.rxtargram.base

import android.content.Context
import com.teamrx.rxtargram.login.KakaoSDKAdapter
import smart.base.BApplication

class AppApplication : BApplication() {
    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = baseContext
        KakaoSDKAdapter()
    }
}