package com.teamrx.rxtargram.profile

import android.log.Log
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppActivity
import com.teamrx.rxtargram.databinding.ProfileWriteBinding
import com.teamrx.rxtargram.inject.Injection
import com.teamrx.rxtargram.model.ProfileModel
import com.teamrx.rxtargram.repository.AppDataSource
import smart.base.PP
import smart.util.GalleryLoader

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

}
