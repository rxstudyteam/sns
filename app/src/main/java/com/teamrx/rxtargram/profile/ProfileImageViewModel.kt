package com.teamrx.rxtargram.profile

import android.log.Log
import android.view.View
import androidx.lifecycle.ViewModel
import smart.util.GalleryLoader

class ProfileImageViewModel : ViewModel() {

    fun getUserImage(view: View) {
        Log.e("getUserImage")
        GalleryLoader.builder(view.context)
            .setCrop(true, 100, 100)
//                .setSource(GalleryLoader.eSource.GALLERY)
            .setOnGalleryLoadedListener { Log.e(it) }
            .setOnCancelListener { Log.toast(view.context, "canceled") }
            .load()

    }
}
