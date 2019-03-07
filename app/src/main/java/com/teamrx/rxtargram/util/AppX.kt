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
                        setTag(R.id.text, imageUrl)
                        setTag(R.id.icon, bitmap)
//                        Log.e(getTag(R.id.text))
//                        Log.e(getTag(R.id.icon))
                    }
                    super.setResource(resource)
                }
            })
}
