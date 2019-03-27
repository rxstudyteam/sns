@file:Suppress("LocalVariableName")

package com.teamrx.rxtargram.profile

//import android.log.sano
import android.graphics.Bitmap
import android.log.Log
import android.util.jpegstream
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamrx.rxtargram.model.ProfileModel
import com.teamrx.rxtargram.repository.AppDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import smart.base.PP

class ProfileViewModel(private var dataSource: AppDataSource) : ViewModel() {
    val name: MutableLiveData<String> = MutableLiveData()
    val email: MutableLiveData<String> = MutableLiveData()
    val profile_url: MutableLiveData<String> = MutableLiveData()

    companion object {
        private const val DEFAULT_PROFILE_URL = "https://firebasestorage.googleapis.com/v0/b/rxteam-sns.appspot.com/o/profile%2Funnamed.png?alt=media&token=bd08fa0e-84b4-438c-8f25-c7014075bf6e"
    }

    suspend fun getProfile(user_id: String): ProfileModel {
        return dataSource.getProfile(PP.user_id)
    }

    suspend fun updateProfile() {
        val profile = if (PP.user_id == PP.deviceid) ProfileModel() else dataSource.getProfile(PP.user_id)
        Log.w(profile)
        name.value = profile.name
        email.value = profile.email
        profile_url.value = profile.profile_url ?: DEFAULT_PROFILE_URL
    }

    suspend fun saveProfile(name: String, email: String, profile: Pair<String, Bitmap>?) {
        if (PP.user_id == PP.deviceid) {
            join(name, email, profile?.second)
        } else {
            update(PP.user_id, name, email, profile)
        }
    }

    private suspend fun join(name: String, email: String, bitmap: Bitmap?) {
        Log.e(0, "join", name, email, bitmap)
        val id = dataSource.uploadToFireStorageUserImage(bitmap?.jpegstream!!)
        val user_image_url = dataSource.getDownloadUrl(id)
        val user_id = dataSource.join(name, email, user_image_url)

        this.name.value = name
        this.email.value = email
        this.profile_url.value = user_image_url
        PP.user_id = user_id
    }

    //    private suspend fun update(user_id: String, name: String?, email: String?, profile : Pair<> profile_url: String?, bitmap: Bitmap?) {
    private suspend fun update(user_id: String, name: String?, email: String?, profile: Pair<String, Bitmap>?) {
        Log.e(0, /*sano(),*/ "update", user_id, name, email, profile)

        profile?.run {
            if (profile_url.value != first) {
                val id = dataSource.uploadToFireStorageUserImage(second.jpegstream)
                profile_url.value = dataSource.getDownloadUrl(id)
            }
        }

        dataSource.setProfile(user_id, name, email, profile_url.value)

        //UI Update
        this.name.value = name
        this.email.value = email
    }

    fun getTitle() = if (PP.user_id == PP.deviceid) "회원가입" else "프로필"

    var mainScope = CoroutineScope(Dispatchers.Main)
    //!!DO NOT HOF
    fun getUserImage(view: View) {
        mainScope.launch {
            profile_url.value = dataSource.loadGalleryLoad(view.context) ?: DEFAULT_PROFILE_URL
        }
    }
}

