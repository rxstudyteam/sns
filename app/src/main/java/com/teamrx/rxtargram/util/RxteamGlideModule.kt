package com.teamrx.rxtargram.util

import android.content.Context
import android.util.Log
import com.bumptech.glide.BuildConfig
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

/**
 * https://bumptech.github.io/glide/doc/configuration.html
 *
 * build.gradle requirement
 * apply plugin: 'kotlin-kapt'
 * implementation 'com.github.bumptech.glide:glide:x.x.x'
 * annotationProcessor 'com.github.bumptech.glide:compiler:x.x.x'
 * kapt 'com.github.bumptech.glide:compiler:x.x.x'
 */
@GlideModule
class RxteamGlideModule: AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
        if(BuildConfig.DEBUG) {
            builder.setLogLevel(Log.VERBOSE)
        }

        //cache https://bumptech.github.io/glide/doc/caching.html#clearing-the-disk-cache
        builder.setMemoryCache(LruResourceCache(Runtime.getRuntime().freeMemory() / 4))

        Log.i("Glide", "free Size /4 ${Runtime.getRuntime().freeMemory() / 4}")

        val diskCacheSizeBytes = 1024 * 1024 * 100 // 100 MB
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, diskCacheSizeBytes.toLong()))
    }
}