package com.teamrx.rxtargram.base

import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import smart.base.BApplication
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class AppApplication : BApplication() {
    override fun onCreate() {
        super.onCreate()
        hashkeyForDaumMap(applicationContext)
    }

    fun hashkeyForDaumMap(context: Context) {
        try {
            val info = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                android.util.Log.e(context.packageName, Base64.encodeToString(md.digest(), Base64.NO_WRAP))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

    }

}