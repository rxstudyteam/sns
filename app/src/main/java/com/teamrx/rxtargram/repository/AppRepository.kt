package com.teamrx.rxtargram.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.teamrx.rxtargram.model.Post
import com.teamrx.rxtargram.model.ProfileModel
import java.io.InputStream

class AppRepository(private val remoteAppDataSource: RemoteAppDataSource) : AppDataSource {

    companion object {
        private var INSTANCE: AppRepository? = null
        fun getInstance(remoteDataSource: RemoteAppDataSource) = INSTANCE
                ?: synchronized(AppRepository::class.java) {
                    INSTANCE ?: AppRepository(remoteDataSource).also { INSTANCE = it }
                }

        private const val DEFAULT_PROFILE_URL = "https://firebasestorage.googleapis.com/v0/b/rxteam-sns.appspot.com/o/profile%2Funnamed.png?alt=media&token=bd08fa0e-84b4-438c-8f25-c7014075bf6e"
    }

    override fun getProfile(user_id: String): ProfileModel {
        return remoteAppDataSource.getProfile(user_id).apply { profile_url = profile_url ?: DEFAULT_PROFILE_URL }
    }

    override fun getPosts(): LiveData<List<Post>> {
        return remoteAppDataSource.getPosts()
    }

    override suspend fun loadGalleryLoad(context: Context): String? {
        return remoteAppDataSource.loadGalleryLoad(context)
    }

    override suspend fun uploadToFireStorage(user_id: String, stream: InputStream): String? {
        return remoteAppDataSource.uploadToFireStorage(user_id, stream)
    }

    override suspend fun setProfile(user_id: String, name: CharSequence?, email: CharSequence?, profile_url: String?): Boolean {
        return remoteAppDataSource.setProfile(user_id, name, email, profile_url)
    }

    override suspend fun join(name: CharSequence, email: CharSequence): String {
        return remoteAppDataSource.join(name, email)
    }

}