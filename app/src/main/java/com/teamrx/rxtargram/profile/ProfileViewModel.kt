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
    val name: MutableLiveData<String> = MutableLiveData()
    val email: MutableLiveData<String> = MutableLiveData()
    val profile_url: MutableLiveData<String> = MutableLiveData()

    companion object {
        private const val DEFAULT_PROFILE_URL = "https://firebasestorage.googleapis.com/v0/b/rxteam-sns.appspot.com/o/profile%2Funnamed.png?alt=media&token=bd08fa0e-84b4-438c-8f25-c7014075bf6e"
    }

    suspend fun updateProfile() {
        var user_id = PP.user_id.get()
//            user_id = "KxUypfZKf2cKmJs4jOeU"
        val profile = if (user_id.isNullOrBlank()) ProfileModel() else dataSource.getProfile(user_id)
        Log.w(profile)
        name.value = profile.name
        email.value = profile.email
        profile_url.value = profile.profile_url ?: DEFAULT_PROFILE_URL
    }

    suspend fun saveProfile(name: String, email: String, profile_url: String?, bitmap: Bitmap?) {
//        return join(name, email, bitmap)
        val userId = PP.user_id.get()
        if (userId.isNullOrBlank()) {
            join(name, email, bitmap)
        } else {
            update(userId, name, email, profile_url, bitmap)
        }
    }

    private suspend fun join(name: String, email: String, bitmap: Bitmap?) {
        Log.e(0, "join", name, email, bitmap)
//        Log.e(1, sano())
        val user_id = dataSource.join(name, email)
//        Log.w(2, nano(), user_id)
        dataSource.uploadToFireStorage(user_id, bitmap?.toStream()!!)
//        Log.w(3, nano())
        var image_url = dataSource.getDownloadUrl(user_id)
//        Log.w(4, nano(), image_url)
        dataSource.setProfile(user_id, null, null, image_url)
//        Log.w(5, nano())
        this@ProfileViewModel.name.value = name
        this@ProfileViewModel.email.value = email
        this@ProfileViewModel.profile_url.value = image_url
//        Log.w(6, nano())
        PP.user_id.set(user_id)
        Log.w(0)
    }

    private suspend fun update(user_id: String, name: String?, email: String?, profile_url: String?, bitmap: Bitmap?) {
        Log.e(0, sano(), "update", user_id, name, email, profile_url, bitmap)
        var profileUrl: String? = profile_url
        if (this@ProfileViewModel.profile_url.value != profileUrl) {
//            Log.w(3, nano(), user_id)
            dataSource.uploadToFireStorage(user_id, bitmap?.toStream()!!)
//            Log.w(4, nano())
            profileUrl = dataSource.getDownloadUrl(user_id)
//            Log.w(5, nano())
        }
//        Log.w(6, nano(), profile_url)
        dataSource.setProfile(user_id
                , name?.takeUnless { this@ProfileViewModel.name.value == name }
                , email?.takeUnless { this@ProfileViewModel.email.value == email }
                , profileUrl?.takeUnless { this@ProfileViewModel.profile_url.value == profileUrl }
        )
//        Log.w(7, nano())
        name?.takeUnless { this@ProfileViewModel.name.value == name }?.let { this@ProfileViewModel.name.value = name }
        email?.takeUnless { this@ProfileViewModel.email.value == email }?.let { this@ProfileViewModel.email.value = email }
        profileUrl?.takeUnless { this@ProfileViewModel.profile_url.value == profileUrl }?.let { this@ProfileViewModel.profile_url.value = profileUrl }
//        Log.w(8, nano())
        Log.w(0, nano())
    }

    fun getTitle() = if (PP.user_id.get().isNullOrBlank()) "회원가입" else "프로필"

    //!!DO NOT HOF
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