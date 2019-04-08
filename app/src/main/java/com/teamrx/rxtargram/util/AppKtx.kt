package com.teamrx.rxtargram.util

import android.graphics.Bitmap
import android.log.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.teamrx.rxtargram.R

@BindingAdapter("load")
fun ImageView.load(imageUrl: String?) {
    Log.e(imageUrl)
    Glide.with(this)
            .setDefaultRequestOptions(RequestOptions().apply {
                placeholder(R.drawable.ic_face_black_24dp)
                error(R.drawable.ic_face_black_24dp)
            })
            .asBitmap()
            .load(imageUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(object : BitmapImageViewTarget(this) {
                override fun setResource(resource: Bitmap?) {
                    resource?.let { bitmap ->
                        setTag(R.id.uri, imageUrl)
                        setTag(R.id.bitmap, bitmap)
                        Log.e("uri", getTag(R.id.uri))
                        Log.e("bitmap", getTag(R.id.bitmap))
                    }
                    super.setResource(resource)
                }
            })
}

fun ImageView.setGlide(uri: String?) {
    Log.e(uri)
    Glide.with(this)
            .setDefaultRequestOptions(RequestOptions().apply {
                placeholder(R.drawable.ic_face_black_24dp)
                error(R.drawable.ic_face_black_24dp)
            })
            .asBitmap()
            .load(uri)
            .into(object : BitmapImageViewTarget(this) {
                override fun setResource(resource: Bitmap?) {
                    resource?.let { bitmap ->
                        setTag(R.id.uri, uri)
                        setTag(R.id.bitmap, bitmap)
                        Log.e("uri", getTag(R.id.uri))
                        Log.e("bitmap", getTag(R.id.bitmap))
                    }
                    super.setResource(resource)
                }
            })
}


