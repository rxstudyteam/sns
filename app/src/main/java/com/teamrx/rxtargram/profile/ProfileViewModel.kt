@file:Suppress("LocalVariableName")

package com.teamrx.rxtargram.profile

import android.graphics.Bitmap
import android.log.Log
import android.log.nano
import android.log.sano
import android.view.View
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
    private lateinit var keepProfile: ProfileModel
    val name: MutableLiveData<String> = MutableLiveData()
    val email: MutableLiveData<String> = MutableLiveData()
    val profile_url: MutableLiveData<String> = MutableLiveData()
    val loading: MutableLiveData<Boolean> = MutableLiveData()

    companion object {
        private const val DEFAULT_PROFILE_URL = "https://firebasestorage.googleapis.com/v0/b/rxteam-sns.appspot.com/o/profile%2Funnamed.png?alt=media&token=bd08fa0e-84b4-438c-8f25-c7014075bf6e"
    }

    fun updateProfile() {
        Log.e(0)
        CoroutineScope(Dispatchers.Main).launch {
            Log.e(0)
            var user_id = PP.user_id.get()
            user_id = "KxUypfZKf2cKmJs4jOeU"

            keepProfile = if (user_id.isNullOrBlank()) ProfileModel() else dataSource.getProfile(user_id)
            Log.e(1, keepProfile)

            Log.w(2)
            name.value = keepProfile.name
            email.value = keepProfile.email
            profile_url.value = keepProfile.profile_url ?: DEFAULT_PROFILE_URL
            Log.w(0)
        }
        Log.w(0)
    }

    fun saveProfile(name: String, email: String, profile_url: String?, bitmap: Bitmap?) {
        val userId = PP.user_id.get()
        if (userId.isNullOrBlank()) {
            join(name, email, bitmap)
        } else {
            update(userId, name, email, profile_url, bitmap)
        }
    }

    private fun join(name: String, email: String, bitmap: Bitmap?) {
        Log.e(0, "join", name, email, bitmap)
        CoroutineScope(Dispatchers.Main).launch {
            Log.e(1, sano())
            loading.value = true
            Log.e(2, nano())
            val user_id = dataSource.join(name, email)
            Log.w(3, nano(), user_id)
            dataSource.uploadToFireStorage(user_id, bitmap?.toStream()!!)
            Log.w(4, nano())
            var image_url = dataSource.getDownloadUrl(user_id)
            Log.w(5, nano(), image_url)
            dataSource.setProfile(user_id, null, null, image_url)
            Log.w(6, nano())
            this@ProfileViewModel.name.value = name
            this@ProfileViewModel.email.value = email
            this@ProfileViewModel.profile_url.value = image_url
            loading.value = false
            Log.w(7, nano())
            PP.user_id.set(user_id)
        }
        Log.w(100, "join", name, email, bitmap)
    }

    private fun update(user_id: String, name: String?, email: String?, profile_url: String?, bitmap: Bitmap?) {
        Log.w(0, sano(), "update")
        CoroutineScope(Dispatchers.Main).launch {
            Log.e(1, nano())
            loading.value = true
            Log.e(2, nano())
            var profile_url: String? = profile_url
            if (this@ProfileViewModel.profile_url.value != profile_url) {
                Log.w(3, nano(), user_id)
                dataSource.uploadToFireStorage(user_id, bitmap?.toStream()!!)
                Log.w(4, nano())
                profile_url = dataSource.getDownloadUrl(user_id)
                Log.w(5, nano())
            }
            Log.w(6, nano(), profile_url)
            dataSource.setProfile(user_id
                    , name?.takeUnless { this@ProfileViewModel.name.value == name }
                    , email?.takeUnless { this@ProfileViewModel.email.value == email }
                    , profile_url?.takeUnless { this@ProfileViewModel.profile_url.value == profile_url }
            )
            Log.w(7, nano())
            name?.takeUnless { this@ProfileViewModel.name.value == name }?.let { this@ProfileViewModel.name.value = name }
            email?.takeUnless { this@ProfileViewModel.email.value == email }?.let { this@ProfileViewModel.email.value = email }
            profile_url?.takeUnless { this@ProfileViewModel.profile_url.value == profile_url }?.let { this@ProfileViewModel.profile_url.value = profile_url }
            loading.value = false
            Log.w(8, nano())
        }
        Log.w(99, nano())
    }

    fun getTitle() = if (PP.user_id.get().isNullOrBlank()) "회원가입" else "프로필"

    fun getUserImage(view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            profile_url.value = dataSource.loadGalleryLoad(view.context) ?: DEFAULT_PROFILE_URL
        }
    }
}

private fun Bitmap.toStream(): InputStream {
    val bos = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 100, bos)
    val bytes = bos.toByteArray()
    return ByteArrayInputStream(bytes)
}