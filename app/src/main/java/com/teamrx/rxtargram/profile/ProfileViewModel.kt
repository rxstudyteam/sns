package com.teamrx.rxtargram.profile

import android.log.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamrx.rxtargram.model.ProfileModel
import com.teamrx.rxtargram.repository.AppDataSource
import smart.base.PP
import smart.util.GalleryLoader
import smart.util.dp

class ProfileViewModel(private var dataSource: AppDataSource) : ViewModel() {
    lateinit var profileModel: MutableLiveData<ProfileModel>
    fun getProfile(): LiveData<ProfileModel> {
        Log.e(1)
        if (!::profileModel.isInitialized) {
            Log.e(2)
            profileModel = MutableLiveData()
            val userId = PP.user_id.get("")!!
            profileModel.value = dataSource.getProfile(userId)
        }
        Log.e(profileModel.value)
        return profileModel
    }

    fun saveProfile(name: String?, email: String?, imageUrl: String?): Boolean {
        val userId = PP.user_id.get()
        return if (userId.isNullOrEmpty()) {
            Log.w("join")
            try {
                dataSource.join(name!!, email!!, imageUrl)
            } catch (e: Exception) {
                false
            }
        } else {
            Log.w("update")
            try {
                dataSource.setProfile(name, email, imageUrl)
            } catch (e: Exception) {
                false
            }
        }
    }

    fun getTitle() = if (PP.user_id.get().isNullOrBlank()) "회원가입" else "프로필"
    fun getUserImage(view: View) {
        Log.e("getUserImage")

        GalleryLoader.builder(view.context)
            .setCrop(true, 100.dp, 100.dp)
//                .setSource(GalleryLoader.eSource.GALLERY)
            .setOnGalleryLoadedListener {
                profileModel.value = profileModel.value?.copy(profile_url = it.toString()) ?: ProfileModel(profile_url = it.toString())
            }
            .setOnCancelListener { Log.toast(view.context, "canceled") }
            .load()
    }
}
