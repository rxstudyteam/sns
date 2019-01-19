package com.teamrx.rxtargram.profile

import android.log.Log
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamrx.rxtargram.model.MProfile
import com.teamrx.rxtargram.repository.AppDataSource
import smart.base.PP


class ProfileViewModel(private var dataSource: AppDataSource) : ViewModel() {

//    //가입
//    fun join(email: String, name: String, profile_url: String? = null) {
//        dataSource.join(MProfile(email, name, profile_url))
//    }

    private lateinit var profile: MutableLiveData<MProfile>
    fun getProfile(): LiveData<MProfile> {
        Log.e()
        if (!::profile.isInitialized) {
            Log.e()
            profile = MutableLiveData()
            val userId = PP.user_id.get("")!!
            dataSource.getProfile(userId) {
                Log.w(it)
            }
        }
        Log.e()
        return profile
    }

//    val user_id: MutableLiveData<String> = MutableLiveData()
//    val email: MutableLiveData<String> = MutableLiveData()
//    val name: MutableLiveData<String> = MutableLiveData()
    val profile_url: MutableLiveData<String> = MutableLiveData()

//    fun getProfile(): LiveData<MProfile> {
//        Log.e()
//        if (!::profile.isInitialized) {
//            Log.e()
//            profile = MutableLiveData()
//            val userId = PP.user_id.get("")!!
//            dataSource.getProfile(userId) {
//                Log.w(it)
////                user_id.postValue(userId)
////                email.postValue(it?.email)
////                name.postValue(it?.name)
////                profile_url.postValue(it?.profile_url)
//            }
//        }
//        Log.e()
//        return profile
//    }

//    private lateinit var profiles: MutableLiveData<Pair<String, MProfile?>>
//    fun getProfiles(): MutableLiveData<Pair<String, MProfile?>> {
//
//        if (!::profiles.isInitialized) {
//            profiles = MutableLiveData()
//            dataSource.getProfiles()
//        }
//        return profiles
//    }

//        val database = FirebaseDatabase.getInstance()
//        val ref = database.getReference().g
//        ref.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // This method is called once with the initial value and again whenever data at this location is updated.
//                val value = dataSnapshot.getValue(String::class.java)
//                Log.e(value)
//            }
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Log.e(error)
//            }
//        })

}
