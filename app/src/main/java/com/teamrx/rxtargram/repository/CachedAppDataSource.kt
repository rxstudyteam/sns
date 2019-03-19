@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package com.teamrx.rxtargram.repository

import android.content.Context
import androidx.collection.LruCache
import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.model.ProfileModel
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

    const val USER_COLLECTION = "user"
    const val POST_COLLECTION = "post"

    object USER_DOCUMENT {
        const val NAME = "name"
        const val EMAIL = "email"
        const val PROFILE_URL = "profile_url"
        const val FOLLOWS = "follows"
        const val FOLLOWERS = "followers"
    }

    object POST_DOCUMENT {
        const val USER_ID = "user_id"
        const val TITLE = "title"
        const val CONTENT = "content"
        const val IMAGES = "images"
        const val PARENT_POST_NO = "parent_post_no"
        const val LIKES = "likes"
        const val CREATED_AT = "created_at"
    }

    class UserCache : LruCache<String, ProfileModel>(10000)

    val users: UserCache = UserCache()

    object STORAGE {
        const val POST = "images/"
        const val USER = "profile/"
    }

    override suspend fun getProfile(user_id: String): ProfileModel {
        if (user_id.isBlank()) {
            return ProfileModel()
        }

        return users.get(user_id) ?: RemoteAppDataSource.getProfile(user_id).also { users.put(user_id,it) }
    }
}