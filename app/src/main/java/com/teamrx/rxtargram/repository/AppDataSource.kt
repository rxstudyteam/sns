package com.teamrx.rxtargram.repository

import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.model.Post
import android.content.Context
import com.teamrx.rxtargram.model.ProfileModel
import java.io.InputStream

interface AppDataSource {
    // 글 목록 가져오기
    fun getPosts(callback: (List<Post>) -> Unit)

    // 글 상세정보 가져오기
    fun getPostById(post_id: String, callback: (Post) -> Unit)

    // 댓글 목록 가져오기
    fun getComments(post_id: String, callback: (List<CommentDTO>) -> Unit)

    // 댓글 추가하기
    fun addComment(parent_post_id: String, user_id: String, content: String, callback: (Boolean) -> Unit)

    //프로필가져오기
    suspend fun getProfile(user_id: String): ProfileModel

    //내프로필변경
    suspend fun setProfile(user_id: String, name: CharSequence?, email: CharSequence?, profile_url: String?): Boolean

    //가입
    suspend fun join(name: CharSequence, email: CharSequence): String

    //사진가져오기
    suspend fun loadGalleryLoad(context: Context): String?

    //사진업로드하기
    suspend fun uploadToFireStorage(user_id: String, stream: InputStream)

    suspend fun getDownloadUrl(user_id: String): String?

}