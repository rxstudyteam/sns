package com.teamrx.rxtargram.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.teamrx.rxtargram.model.Post
import com.teamrx.rxtargram.model.ProfileModel
import java.io.InputStream

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

    override suspend fun getProfile(user_id: String): ProfileModel {
        return remoteAppDataSource.getProfile(user_id)
    }

    override suspend fun loadGalleryLoad(context: Context): String? {
        return remoteAppDataSource.loadGalleryLoad(context)
    }

    override suspend fun uploadToFireStorage(user_id: String, stream: InputStream) {
        remoteAppDataSource.uploadToFireStorage(user_id, stream)
    }

    override suspend fun getDownloadUrl(user_id: String): String? {
        return remoteAppDataSource.getDownloadUrl(user_id)
    }

    override suspend fun setProfile(user_id: String, name: CharSequence?, email: CharSequence?, profile_url: String?): Boolean {
        return remoteAppDataSource.setProfile(user_id, name, email, profile_url)
    }

    override suspend fun join(name: CharSequence, email: CharSequence): String {
        return remoteAppDataSource.join(name, email)
    }
}