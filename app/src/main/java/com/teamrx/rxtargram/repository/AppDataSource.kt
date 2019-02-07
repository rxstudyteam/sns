package com.teamrx.rxtargram.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.teamrx.rxtargram.model.Post
import com.teamrx.rxtargram.model.ProfileModel
import java.io.InputStream

interface AppDataSource {
    // 글 목록 가져오기
    fun getPosts(): LiveData<List<Post>>

    //프로필가져오기
    fun getProfile(user_id: String): ProfileModel
    //내프로필변경
    suspend fun setProfile(user_id: String, name: CharSequence?, email: CharSequence?, profile_url: String?): Boolean
    //가입
    suspend fun join(name: CharSequence, email: CharSequence): String

    suspend fun loadGalleryLoad(context: Context): String?

    suspend fun uploadToFireStorage(user_id: String, stream: InputStream): String?
}