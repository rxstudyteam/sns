package com.teamrx.rxtargram.profile

import android.graphics.Bitmap
import android.log.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamrx.rxtargram.model.ProfileModel
import com.teamrx.rxtargram.repository.AppDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import smart.base.PP
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ProfileViewModel(private var dataSource: AppDataSource) : ViewModel() {
    lateinit var profileModel: MutableLiveData<ProfileModel>
    private lateinit var keepProfile: ProfileModel

    fun getProfile(): LiveData<ProfileModel> {
        Log.e(1)
        if (!::profileModel.isInitialized) {
            Log.e(2)
            profileModel = MutableLiveData()
            val userId = PP.user_id.get()!!
            keepProfile = dataSource.getProfile(userId)
            Log.w(3, keepProfile)
            profileModel.value = keepProfile
            Log.w(2, profileModel.value)
        }
        Log.w(1, profileModel.value)
        return profileModel
    }

    fun saveProfile(name: CharSequence, email: CharSequence, img: Bitmap?) {
        val userId = PP.user_id.get()
        if (userId.isNullOrEmpty()) {
            join(name, email, img)
//        } else {
//            setProfile(name, email, img)
        }
    }

    private fun join(name: CharSequence, email: CharSequence, img: Bitmap?) {
        Log.e(0, "join", name, email, img)
        CoroutineScope(Dispatchers.Main).launch {
            Log.e(1)
            val user_id = dataSource.join(name, email)
            Log.w(1, user_id)

            Log.e(2)
            var url = dataSource.uploadToFireStorage(user_id, img?.toStream()!!)
            Log.w(2, user_id)

            dataSource.setProfile(user_id, null, null, url)

        }
        Log.w(99, "join", name, email, img)
    }

//    private fun setProfile(name: CharSequence, email: CharSequence, image_url: Bitmap?) {
//        val imageUrl = ""
//        Log.w("update")
//        dataSource.setProfile(name, email, img.toStream()!!)
//    }

    fun getTitle() = if (PP.user_id.get().isNullOrBlank()) "회원가입" else "프로필"

    fun getUserImage(view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            val context = view.context
            var profile_url = dataSource.loadGalleryLoad(context)
            profile_url?.run { profileModel.value = profileModel.value?.copy(profile_url = profile_url) ?: ProfileModel(profile_url = profile_url) }
        }
    }
}

private fun Bitmap.toStream(): InputStream {
    val bos = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 100, bos)
    val bytes = bos.toByteArray()
    return ByteArrayInputStream(bytes)
}