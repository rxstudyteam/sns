package com.teamrx.rxtargram.repository

import androidx.lifecycle.LiveData
import com.teamrx.rxtargram.model.Post
import com.teamrx.rxtargram.model.ProfileModel

class AppRepository(private val remoteAppDataSource: RemoteAppDataSource) : AppDataSource {
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun setProfile(name: String?, email: String?, profile_url: String?) = remoteAppDataSource.setProfile(name, email, profile_url)

    override fun join(name: String, email: String, profile_url: String?) = remoteAppDataSource.join(name, email, profile_url)
    override fun getProfile(user_id: String): ProfileModel? {
        return remoteAppDataSource.getProfile(user_id)?.apply {
            profile_url = "content//com.teamrx.rxtargram.provider/gloader/result_20190130_174417_8270220685296630039.jpg"
        }
    }

    override fun getPosts(): LiveData<List<Post>> {
        return remoteAppDataSource.getPosts()
    }

    companion object {
        private var INSTANCE: AppRepository? = null
        fun getInstance(remoteDataSource: RemoteAppDataSource) = INSTANCE
                ?: synchronized(AppRepository::class.java) {
                    INSTANCE ?: AppRepository(remoteDataSource).also { INSTANCE = it }
                }
    }
}