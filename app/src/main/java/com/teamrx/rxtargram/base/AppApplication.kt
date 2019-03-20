package com.teamrx.rxtargram.base

import android.content.Context
import com.kakao.auth.KakaoSDK
import com.teamrx.rxtargram.login.KakaoSDKAdapter
import smart.base.BApplication

class AppApplication : BApplication() {
    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        KakaoSDK.init(KakaoSDKAdapter())
    }
}