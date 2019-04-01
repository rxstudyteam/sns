package com.teamrx.rxtargram.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
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
            if (Build.VERSION.SDK_INT >= 28) {
                @SuppressLint("WrongConstant")
                val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                val md = MessageDigest.getInstance("SHA")
                for (signature in packageInfo.signingInfo.apkContentsSigners) {
                    md.update(signature.toByteArray())
                    android.util.Log.e(context.packageName, Base64.encodeToString(md.digest(), Base64.NO_WRAP))
                }
            } else {
                val info = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
                val md = MessageDigest.getInstance("SHA")
                for (signature in info.signatures) {
                    md.update(signature.toByteArray())
                    android.util.Log.e(context.packageName, Base64.encodeToString(md.digest(), Base64.NO_WRAP))
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }
}