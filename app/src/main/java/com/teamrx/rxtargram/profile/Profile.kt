package com.teamrx.rxtargram.profile

import android.log.Log
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppActivity
import com.teamrx.rxtargram.databinding.ProfileWriteBinding
import com.teamrx.rxtargram.inject.Injection
import com.teamrx.rxtargram.model.ProfileModel
import com.teamrx.rxtargram.repository.AppDataSource
import smart.base.PP

class Profile : AppActivity() {
    private lateinit var bb: ProfileWriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bb = DataBindingUtil.setContentView(this, R.layout.profile_write)
        bb.profileViewModel = ViewModelProviders.of(mActivity, Injection.provideViewModelFactory()).get(ProfileViewModel::class.java)
        bb.setLifecycleOwner(this)
    }
}

class ProfileViewModel(private var dataSource: AppDataSource) : ViewModel() {
    lateinit var profileModel: MutableLiveData<ProfileModel>

    fun getProfile(): LiveData<ProfileModel> {
        if (!::profileModel.isInitialized) {
            profileModel = MutableLiveData()
            val userId = PP.user_id.get("")!!
            dataSource.getProfile(userId) {
                Log.w(it)
                profileModel.value = ProfileModel(it.name, it.email, it.profile_url)
            }
        }
        return profileModel
    }

    fun getProfile2(): LiveData<ProfileModel> {
        if (!::profileModel.isInitialized) {
            profileModel = MutableLiveData()
            val userId = PP.user_id.get("")!!
            profileModel.value = dataSource.getProfile2(userId)
        }
        return profileModel
    }
}

//@BindingAdapter("bind:imageUrl")
//fun ImageView.loadImage(imageUrl: String?) {
//    Log.e(imageUrl)
//    Glide.with(context)
//            .load(imageUrl)
//            .into(this)
//}

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