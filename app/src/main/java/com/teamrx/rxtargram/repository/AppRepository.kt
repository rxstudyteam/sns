package com.teamrx.rxtargram.repository

import android.content.Context
import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.model.Post
import com.teamrx.rxtargram.model.PostImages
import com.teamrx.rxtargram.model.ProfileModel
import java.io.InputStream

class AppRepository(private val remoteAppDataSource: RemoteAppDataSource) : AppDataSource {
    override fun modifyPost(post: Post, callback: (Boolean) -> Unit) = remoteAppDataSource.modifyPost(post, callback)

    override fun getPosts(callback: (List<Post>) -> Unit) {
        remoteAppDataSource.getPosts(callback)
    }

    override fun getPostById(post_id: String, callback: (Post) -> Unit) {
        remoteAppDataSource.getPostById(post_id, callback)
    }

    override fun getComments(post_id: String, callback: (List<CommentDTO>) -> Unit) {
        remoteAppDataSource.getComments(post_id, callback)
    }

    override fun addComment(parent_post_id: String, user_id: String, content: String, callback: (Boolean) -> Unit) {
        remoteAppDataSource.addComment(parent_post_id, user_id, content, callback)
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

    override fun getPostImages(post_id: String?, callback: (List<PostImages>) -> Unit) {
        remoteAppDataSource.getPostImages(post_id, callback)
    }

    companion object {
        private var INSTANCE: AppRepository? = null
        fun getInstance(remoteDataSource: RemoteAppDataSource) = INSTANCE ?: synchronized(AppRepository::class.java) {
            INSTANCE ?: AppRepository(remoteDataSource).also { INSTANCE = it }
        }
    }
}