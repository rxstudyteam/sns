@file:Suppress("LocalVariableName")

package com.teamrx.rxtargram.profile

import android.graphics.Bitmap
import android.log.Log
import android.util.jpegstream
//import android.log.sano
import android.util.toStream
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

    suspend fun saveProfile(name: String, email: String, profile: Pair<String, Bitmap>?) {
//        return join(name, email, bitmap)
        val userId = PP.user_id.get()
        if (userId.isNullOrBlank()) {
            join(name, email, profile?.second)
        } else {
            update(userId, name, email, profile)
        }
    }

    private suspend fun join(name: String, email: String, bitmap: Bitmap?) {
        Log.e(0, "join", name, email, bitmap)
        val user_id = dataSource.join(name, email)
        dataSource.uploadToFireStorage(user_id, bitmap?.jpegstream!!)
        val image_url = dataSource.getDownloadUrl(user_id)
        dataSource.setProfile(user_id, null, null, image_url)
        this.name.value = name
        this.email.value = email
        this.profile_url.value = image_url
        PP.user_id.set(user_id)
    }

    //    private suspend fun update(user_id: String, name: String?, email: String?, profile : Pair<> profile_url: String?, bitmap: Bitmap?) {
    private suspend fun update(user_id: String, name: String?, email: String?, profile: Pair<String, Bitmap>?) {
        Log.e(0, /*sano(),*/ "update", user_id, name, email, profile)

        profile?.run {
            if (profile_url.value != first) {
                dataSource.uploadToFireStorage(user_id, second.jpegstream)
                profile_url.value = dataSource.getDownloadUrl("profile/${user_id}")
            }
        }

        dataSource.setProfile(user_id, name, email, profile_url.value)

        //UI Update
        this.name.value = name
        this.email.value = email
    }

    fun getTitle() = if (PP.user_id.get().isNullOrBlank()) "회원가입" else "프로필"

    var mainScope = CoroutineScope(Dispatchers.Main)
    //!!DO NOT HOF
    fun getUserImage(view: View) {
        mainScope.launch {
            profile_url.value = dataSource.loadGalleryLoad(view.context) ?: DEFAULT_PROFILE_URL
        }
    }
}
