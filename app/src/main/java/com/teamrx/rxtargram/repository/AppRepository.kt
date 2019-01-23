package com.teamrx.rxtargram.repository

import androidx.lifecycle.LiveData
import com.teamrx.rxtargram.model.Post

class AppRepository(private val remoteAppDataSource: RemoteAppDataSource) : AppDataSource {
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun setProfile(name: String?, email: String?, profile_url: String?) = remoteAppDataSource.setProfile(name, email, profile_url)

    override fun join(name: String, email: String, profile_url: String?) = remoteAppDataSource.join(name, email, profile_url)

    override fun getProfile(user_id: String) = remoteAppDataSource.getProfile(user_id)

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