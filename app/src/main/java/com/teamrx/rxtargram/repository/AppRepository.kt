package com.teamrx.rxtargram.repository

import androidx.lifecycle.LiveData
import com.teamrx.rxtargram.model.Post
import com.teamrx.rxtargram.model.ProfileModel

class AppRepository(private val remoteAppDataSource: RemoteAppDataSource) : AppDataSource {
    override fun modifyPost(post: Post, callback: (Boolean) -> Unit) = remoteAppDataSource.modifyPost(post, callback)

    override fun join(profileModel: ProfileModel) = remoteAppDataSource.join(profileModel)

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