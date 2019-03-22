@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package com.teamrx.rxtargram.repository

import android.content.Context
import androidx.collection.LruCache
import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.model.ProfileModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import java.io.InputStream

@Suppress("ClassName", "unused")
object CachedAppDataSource : AppDataSource {
    override fun addComment(parent_post_id: String, user_id: String, content: String, callback: (Boolean) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCommentSnapshotListener(post_id: String, callback: (List<CommentDTO>) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun join(name: CharSequence, email: CharSequence, user_image_url: CharSequence?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun setProfile(user_id: String, name: CharSequence?, email: CharSequence?, profile_url: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun uploadToFireStorageUserImage(stream: InputStream): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun uploadToFireStoragePostImage(stream: InputStream): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun loadGalleryLoad(context: Context): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getDownloadUrl(image_id: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPost(post_id: String, callback: (PostDTO) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun addPost(postDTO: PostDTO): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPost(post: PostDTO, callback: (Boolean) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPostSnapshotListener(callback: (List<PostDTO>) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    object UserCache : LruCache<String, ProfileModel>(10000) {
        override fun create(key: String): ProfileModel? {
            var result: ProfileModel? = null
            runBlocking(Dispatchers.IO) {
                result = RemoteAppDataSource.getProfile(user_id = key)
            }
            return result
        }
    }

    object STORAGE {
        const val POST = "images/"
        const val USER = "profile/"
    }

    override suspend fun getProfile(user_id: String): ProfileModel {
        if (user_id.isBlank()) {
            return ProfileModel()
        }

        return UserCache.get(user_id)!!
    }
}