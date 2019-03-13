package com.teamrx.rxtargram.repository

import android.content.Context
import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.model.Post
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.model.ProfileModel
import kotlinx.coroutines.channels.ReceiveChannel
import java.io.InputStream

interface AppDataSource {
    // 글 목록 가져오기
    fun getPosts(callback: (List<Post>) -> Unit)

    // 글 상세정보 가져오기
    fun getPostById(post_id: String, callback: (Post) -> Unit)

    // 댓글 목록 가져오기
    suspend fun getComments(post_id: String): ReceiveChannel<List<CommentDTO>>

    // 댓글 추가하기
    suspend fun addComment(parent_post_id: String, user_id: String, content: String): Boolean

    // 댓글 수정하기
    suspend fun modifyComment(comment: CommentDTO): Boolean

    // 댓글 삭제하기
    suspend fun deleteComment(post_id: String): Boolean

    //프로필가져오기
    suspend fun getProfile(user_id: String): ProfileModel

    //내프로필변경
    suspend fun setProfile(user_id: String, name: CharSequence?, email: CharSequence?, profile_url: String?): Boolean

    //가입
    suspend fun join(name: CharSequence, email: CharSequence): String

    suspend fun join(phoneNumber: CharSequence, email: CharSequence, password: CharSequence): String

    // 폰으로 가입하기
    suspend fun joinableFromPhone(phoneNumber: CharSequence): Boolean

    suspend fun createPost(postDTO: PostDTO): String

    //사진가져오기
    suspend fun loadGalleryLoad(context: Context): String?

    // 글 수정
    fun modifyPost(post: Post, callback: (Boolean) -> Unit)

    //사진업로드하기
    suspend fun uploadToFireStorage(user_id: String, stream: InputStream)

    //images
    suspend fun uploadToFireStoragePostImage(image_id: String, stream: InputStream)

    suspend fun getDownloadUrl(user_id: String): String?

}