package com.teamrx.rxtargram.util

import android.log.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("bind:imageUrl")
fun ImageView.loadImage(imageUrl: String?) {
    Log.e(imageUrl)
    Glide.with(this)
            .load(imageUrl)
            .into(this)
}

//@BindingAdapter("time")
//@JvmStatic fun setTime(view: MyView, newValue: Time) {
//    // Important to break potential infinite loops.
//    if (view.time != newValue) {
//        view.time = newValue
//    }
//}

//@BindingAdapter("android:text")
//fun TextView.loadText(text: String?) {
//    Log.e(text)
//    this.text = "~$text~"
//}