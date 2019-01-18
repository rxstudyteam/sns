package com.teamrx.rxtargram.repository

import androidx.lifecycle.LiveData
import com.teamrx.rxtargram.model.Post

class AppRepository(private val remoteAppDataSource: RemoteAppDataSource): AppDataSource {

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