package com.teamrx.rxtargram.profile

import android.log.Log
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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
    private lateinit var vm: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bb = DataBindingUtil.setContentView(this, R.layout.profile_write)
        vm = ViewModelProviders.of(mActivity, Injection.provideViewModelFactory()).get(ProfileViewModel::class.java)
        bb.profileViewModel = vm
        bb.setLifecycleOwner(this)

        supportActionBar?.title = vm.getTitle()
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                saveProfile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveProfile() {
        if (bb.profileViewModel!!.saveProfile()) {
            Toast.makeText(this, "변경됨", Toast.LENGTH_SHORT).show()
        }
    }
}

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

    fun saveProfile(): Boolean {
        if (profileModel.value == null) {
            Log.w("profileModel.value")
            return false
        }

        val userId = PP.user_id.get()
        return if (userId.isNullOrEmpty()) {
            Log.w("join")
            try {
                dataSource.setProfile(profileModel.value!!)
            } catch (e: Exception) {
                false
            }
        } else {
            Log.w("update")
            try {
                dataSource.setProfile(profileModel.value!!)
            } catch (e: Exception) {
                false
            }
        }

    }

    fun getTitle() = if (PP.user_id.get().isNullOrBlank()) "회원가입" else "프로필"

}
