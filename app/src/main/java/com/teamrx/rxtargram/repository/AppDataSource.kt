package com.teamrx.rxtargram.repository

import android.content.Context
import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.model.ProfileModel
import java.io.InputStream

interface AppDataSource {
    //@formatter:off

    //글생성
    suspend fun addPost(postDTO: PostDTO): String
    //글목록Snapshot
    fun setPostSnapshotListener(callback: (List<PostDTO>) -> Unit)
    //글수정
    fun setPost(post: PostDTO, callback: (Boolean) -> Unit)
    //글가져오기
    fun getPost(post_id: String, callback: (PostDTO) -> Unit)

    //댓글추가하기
    suspend fun addComment(parent_post_id: String, user_id: String, content: String): Boolean
    //댓글목록가져오기
    fun setCommentSnapshotListener(parent_post_id: String, callback: (List<CommentDTO>) -> Unit)
    // 댓글 수정하기
    suspend fun modifyComment(post_id: String, comment: CommentDTO): Boolean
    // 댓글 삭제하기
    suspend fun deleteComment(post_id: String): Boolean

    //가입
    suspend fun join(name: CharSequence, email: CharSequence, user_image_url: CharSequence?): String
    //프로필가져오기
    suspend fun getProfile(user_id: String): ProfileModel
    //내프로필변경
    suspend fun setProfile(user_id: String, name: CharSequence?, email: CharSequence?, profile_url: String?): Boolean

    //가입
    suspend fun join(name: CharSequence, email: CharSequence): String

    // 폰으로 가입하기
    suspend fun joinableFromPhone(phoneNumber: CharSequence): Boolean

    suspend fun createPost(postDTO: PostDTO): String

    //user사진업로드하기
    suspend fun uploadToFireStorageUserImage(stream: InputStream): String
    //post사진업로드하기
    suspend fun uploadToFireStoragePostImage(stream: InputStream): String
    //사진가져오기
    suspend fun loadGalleryLoad(context: Context): String?
    //사진URL가져오기
    suspend fun getDownloadUrl(image_id: String): String

//    fun getDownloadUrl(post_image_id: String, callback: (String) -> Unit)
    //사진업로드하기
//    fun uploadToFireStorage(id: String, stream: InputStream, callback: (String) -> Unit)

}