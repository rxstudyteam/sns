package com.teamrx.rxtargram.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.teamrx.rxtargram.model.MProfile
import com.teamrx.rxtargram.model.Post

class AppRepository(private val remoteAppDataSource: RemoteAppDataSource) : AppDataSource {

    override fun join(profile: MProfile) {
        remoteAppDataSource.join(profile)
    }

    override fun getProfile(user_id: String, callback: (MProfile?) -> Unit) {
        remoteAppDataSource.getProfile(user_id, callback)
    }

//    override fun getProfiles(): List<MProfile> {
//        remoteAppDataSource.getProfiles()
//    }

//    override fun getProfile(user_id: String?) {
//        remoteAppDataSource.getProfile(user_id)
//    }

//    override fun setProfile(user_id: String, profile: MProfile?) {
//        remoteAppDataSource.setProfile(user_id, profile)
//    }

    override fun getPosts(): LiveData<List<Post>> {
        return remoteAppDataSource.getPosts()
    }

    companion object {
        private var INSTANCE: AppRepository? = null
        fun getInstance(remoteDataSource: RemoteAppDataSource) = INSTANCE ?: synchronized(AppRepository::class.java) {
            INSTANCE ?: AppRepository(remoteDataSource).also { INSTANCE = it }
        }
    }
}