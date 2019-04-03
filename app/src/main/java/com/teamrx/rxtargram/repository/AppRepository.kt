package com.teamrx.rxtargram.repository

import android.content.Context
import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.model.ProfileModel
import java.io.InputStream

class AppRepository(private val remoteAppDataSource: RemoteAppDataSource) : AppDataSource {

    override fun modifyPost(post: PostDTO, callback: (Boolean) -> Unit) = remoteAppDataSource.modifyPost(post, callback)

    companion object {
        private var INSTANCE: AppRepository? = null
        fun getInstance(remoteDataSource: RemoteAppDataSource) = INSTANCE ?: synchronized(AppRepository::class.java) { INSTANCE ?: AppRepository(remoteDataSource).also { INSTANCE = it } }
    }

    override suspend fun addPost(postDTO: PostDTO): String = remoteAppDataSource.addPost(postDTO)
    override fun setPostSnapshotListener(callback: (List<PostDTO>) -> Unit) = remoteAppDataSource.setPostSnapshotListener(callback)
    override fun getPost(post_id: String, callback: (PostDTO) -> Unit) = remoteAppDataSource.getPost(post_id, callback)
    override fun setPost(post: PostDTO, callback: (Boolean) -> Unit) = remoteAppDataSource.setPost(post, callback)

    override suspend fun addComment(parent_post_id: String, user_id: String, content: String) = remoteAppDataSource.addComment(parent_post_id, user_id, content)
    override fun setCommentSnapshotListener(post_id: String, callback: (List<CommentDTO>) -> Unit) = remoteAppDataSource.setCommentSnapshotListener(post_id, callback)
    override suspend fun modifyComment(post_id: String, comment: CommentDTO): Boolean = remoteAppDataSource.modifyComment(post_id, comment)
    override suspend fun deleteComment(post_id: String): Boolean = remoteAppDataSource.deleteComment(post_id)

    override suspend fun join(phoneNumber: CharSequence, email: CharSequence, password: CharSequence): String {
        return remoteAppDataSource.join(phoneNumber, email, password)
    }

    override suspend fun joinableFromPhone(phoneNumber: CharSequence): Boolean {
        return remoteAppDataSource.joinableFromPhone(phoneNumber)
    }

    override suspend fun join(name: CharSequence, email: CharSequence, user_image_url: CharSequence?): String = remoteAppDataSource.join(name, email, user_image_url)
    override suspend fun getProfile(user_id: String): ProfileModel = remoteAppDataSource.getProfile(user_id)
    override suspend fun setProfile(user_id: String, name: CharSequence?, email: CharSequence?, profile_url: String?): Boolean = remoteAppDataSource.setProfile(user_id, name, email, profile_url)

    override suspend fun uploadToFireStorageUserImage(stream: InputStream): String = remoteAppDataSource.uploadToFireStorageUserImage(stream)
    override suspend fun uploadToFireStoragePostImage(stream: InputStream): String = remoteAppDataSource.uploadToFireStoragePostImage(stream)
    override suspend fun getDownloadUrl(image_id: String): String = remoteAppDataSource.getDownloadUrl(image_id)
    override suspend fun loadGalleryLoad(context: Context): String? = remoteAppDataSource.loadGalleryLoad(context)
//    override fun getDownloadUrl(post_image_id: String, callback: (String) -> Unit) = remoteAppDataSource.getDownloadUrl(post_image_id, callback)
//    override fun uploadToFireStorage(id: String, stream: InputStream, callback: (String) -> Unit) = remoteAppDataSource.uploadToFireStorage(id, stream, callback)

}
