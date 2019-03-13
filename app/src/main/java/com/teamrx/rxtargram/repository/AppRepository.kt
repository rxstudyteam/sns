package com.teamrx.rxtargram.repository

import android.content.Context
import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.model.Post
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.model.ProfileModel
import kotlinx.coroutines.channels.ReceiveChannel
import java.io.InputStream

class AppRepository(private val remoteAppDataSource: RemoteAppDataSource) : AppDataSource {

    override fun modifyPost(post: Post, callback: (Boolean) -> Unit) = remoteAppDataSource.modifyPost(post, callback)

    override fun getPosts(callback: (List<Post>) -> Unit) {
        remoteAppDataSource.getPosts(callback)
    }

    override fun getPostById(post_id: String, callback: (Post) -> Unit) {
        remoteAppDataSource.getPostById(post_id, callback)
    }

    override suspend fun getComments(post_id: String): ReceiveChannel<List<CommentDTO>> {
        return remoteAppDataSource.getComments(post_id)
    }

    override suspend fun addComment(parent_post_id: String, user_id: String, content: String): Boolean {
        return remoteAppDataSource.addComment(parent_post_id, user_id, content)
    }

    override suspend fun modifyComment(comment: CommentDTO): Boolean {
        return remoteAppDataSource.modifyComment(comment)
    }

    override suspend fun deleteComment(post_id: String): Boolean {
        return remoteAppDataSource.deleteComment(post_id)
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

    override suspend fun uploadToFireStoragePostImage(image_id: String, stream: InputStream) {
        remoteAppDataSource.uploadToFireStoragePostImage(image_id, stream)
    }

    override suspend fun getDownloadUrl(user_id: String): String? {
        return remoteAppDataSource.getDownloadUrl(user_id)
    }

    override suspend fun setProfile(
        user_id: String,
        name: CharSequence?,
        email: CharSequence?,
        profile_url: String?
    ): Boolean {
        return remoteAppDataSource.setProfile(user_id, name, email, profile_url)
    }

    override suspend fun join(name: CharSequence, email: CharSequence): String {
        return remoteAppDataSource.join(name, email)
    }

    override suspend fun join(phoneNumber: CharSequence, email: CharSequence, password: CharSequence): String {
        return remoteAppDataSource.join(phoneNumber, email, password)
    }

    override suspend fun joinableFromPhone(phoneNumber: CharSequence): Boolean {
        return remoteAppDataSource.joinableFromPhone(phoneNumber)
    }


    override suspend fun createPost(postDTO: PostDTO): String {
        return remoteAppDataSource.createPost(postDTO)
    }

    companion object {
        private var INSTANCE: AppRepository? = null
        fun getInstance(remoteDataSource: RemoteAppDataSource) = INSTANCE ?: synchronized(AppRepository::class.java) {
            INSTANCE ?: AppRepository(remoteDataSource).also { INSTANCE = it }
        }
    }
}