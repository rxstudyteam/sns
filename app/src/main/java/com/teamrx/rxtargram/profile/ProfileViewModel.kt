@file:Suppress("LocalVariableName")

package com.teamrx.rxtargram.profile

import android.graphics.Bitmap
import android.log.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamrx.rxtargram.model.ProfileModel
import com.teamrx.rxtargram.repository.AppDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import smart.base.PP
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ProfileViewModel(private var dataSource: AppDataSource) : ViewModel() {
    private lateinit var keepProfile: ProfileModel
    val name: MutableLiveData<String> = MutableLiveData()
    val email: MutableLiveData<String> = MutableLiveData()
    val profile_url: MutableLiveData<String> = MutableLiveData()

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

    fun saveProfile(name: String, email: String, image_url: String?, bitmap: Bitmap?) {
        val userId = PP.user_id.get()
        if (userId.isNullOrBlank()) {
            join(name, email, bitmap)
        } else {
            setProfile(userId, name, email, bitmap)
        }
    }

    private fun join(name: String, email: String, img: Bitmap?) {
        runBlocking {
            Log.e(0, "join", name, email, img)
//            var job = CoroutineScope(Dispatchers.Main).launch {
            val job = launch {
                //            launch(Dispatchers.Default) {
                Log.e(1)
                val user_id = dataSource.join(name, email)
                Log.w(1, user_id)

                Log.e(2)
                val image_url = dataSource.uploadToFireStorage(user_id, img?.toStream()!!)
                Log.w(2, user_id)
                dataSource.setProfile(user_id, null, null, image_url)
            }
            Log.w(98, "join", name, email, img)
            job.join()
            Log.w(99, "join", name, email, img)
        }
        Log.w(100, "join", name, email, img)
    }

    private fun setProfile(user_id: String, name: String?, email: String?, img: Bitmap?) {
        Log.w("update")
        CoroutineScope(Dispatchers.Main).launch {
            Log.e(2)
            var image_url = dataSource.uploadToFireStorage(user_id, img?.toStream()!!)
            Log.w(2, user_id)

            dataSource.setProfile(user_id, name, email, image_url)
        }
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