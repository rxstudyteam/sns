package com.teamrx.rxtargram.repository

import android.content.Context
import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.model.ProfileModel
import java.io.InputStream

class AppRepository(private val remoteAppDataSource: RemoteAppDataSource) : AppDataSource {
    override fun uploadToFireStorage(id: String, stream: InputStream, callback: (String) -> Unit) = remoteAppDataSource.uploadToFireStorage(id, stream, callback)

    override fun setPostSnapshotListener(callback: (List<PostDTO>) -> Unit) = remoteAppDataSource.setPostSnapshotListener(callback)

    override fun getDownloadUrl(post_image_id: String, callback: (String) -> Unit) {
        remoteAppDataSource.getDownloadUrl(post_image_id, callback)
    }

    override fun setPost(post: PostDTO, callback: (Boolean) -> Unit) = remoteAppDataSource.setPost(post, callback)

    override fun getPosts(callback: (List<PostDTO>) -> Unit) {
        remoteAppDataSource.getPosts(callback)
    }

    override fun getPost(post_id: String, callback: (PostDTO) -> Unit) {
        remoteAppDataSource.getPost(post_id, callback)
    }

    override fun setCommentSnapshotListener(post_id: String, callback: (List<CommentDTO>) -> Unit) {
        remoteAppDataSource.setCommentSnapshotListener(post_id, callback)
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

    override suspend fun uploadToFireStorageUserImage(stream: InputStream): String {
        return remoteAppDataSource.uploadToFireStorageUserImage(stream)
    }

    override suspend fun uploadToFireStoragePostImage(stream: InputStream): String {
        return remoteAppDataSource.uploadToFireStoragePostImage(stream)
    }

    override suspend fun getDownloadUrl(image_id: String): String = remoteAppDataSource.getDownloadUrl(image_id)

    override suspend fun setProfile(user_id: String, name: CharSequence?, email: CharSequence?, profile_url: String?): Boolean {
        return remoteAppDataSource.setProfile(user_id, name, email, profile_url)
    }

    override suspend fun join(name: CharSequence, email: CharSequence, user_image_url: CharSequence?): String {
        return remoteAppDataSource.join(name, email, user_image_url)
    }

    override suspend fun createPost(postDTO: PostDTO): String {
        return remoteAppDataSource.createPost(postDTO)
    }

    companion object {
        private var INSTANCE: AppRepository? = null
        fun getInstance(remoteDataSource: RemoteAppDataSource) = INSTANCE
                ?: synchronized(AppRepository::class.java) {
                    INSTANCE ?: AppRepository(remoteDataSource).also { INSTANCE = it }
                }
    }
}