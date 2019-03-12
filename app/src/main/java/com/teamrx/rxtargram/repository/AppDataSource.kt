package com.teamrx.rxtargram.repository

import android.content.Context
import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.model.ProfileModel
import java.io.InputStream

interface AppDataSource {
    // 글 목록 가져오기
    fun getPosts(callback: (List<PostDTO>) -> Unit)

    // 글 상세정보 가져오기
    fun getPost(post_id: String, callback: (PostDTO) -> Unit)

    // 댓글 목록 가져오기
    fun setCommentSnapshotListener(post_id: String, callback: (List<CommentDTO>) -> Unit)

    // 댓글 추가하기
    fun addComment(parent_post_id: String, user_id: String, content: String, callback: (Boolean) -> Unit)

    //프로필가져오기
    suspend fun getProfile(user_id: String): ProfileModel

    //내프로필변경
    suspend fun setProfile(user_id: String, name: CharSequence?, email: CharSequence?, profile_url: String?): Boolean

    //가입
    suspend fun join(name: CharSequence, email: CharSequence, user_image_url: CharSequence?): String

    suspend fun createPost(postDTO: PostDTO): String

    //사진가져오기
    suspend fun loadGalleryLoad(context: Context): String?

    // 글 수정
    fun setPost(post: PostDTO, callback: (Boolean) -> Unit)

    //사진업로드하기
    suspend fun uploadToFireStorageUserImage(stream: InputStream): String
    //postimageupload
    suspend fun uploadToFireStoragePostImage(stream: InputStream): String

    fun getDownloadUrl(post_image_id: String, callback: (String) -> Unit)

    suspend fun getDownloadUrl(post_image_id: String): String
    fun setPostSnapshotListener(callback: (List<PostDTO>) -> Unit)
    fun uploadToFireStorage(id: String, stream: InputStream, callback: (String) -> Unit)
}