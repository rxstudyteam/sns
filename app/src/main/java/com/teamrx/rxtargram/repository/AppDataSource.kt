package com.teamrx.rxtargram.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.teamrx.rxtargram.model.Post
import com.teamrx.rxtargram.model.ProfileModel
import java.io.InputStream

interface AppDataSource {
    // 글 목록 가져오기
    fun getPosts(): LiveData<List<Post>>

//    //프로필목록
//    fun getProfiles(): List<MProfile>
//    //내프로필변경
//    fun setProfile(user_id: String, profile: MProfile?)

    //프로필가져오기
    fun getProfile(user_id: String): ProfileModel?

    //내프로필변경
    fun setProfile(name: String?, email: String?, profile_url: String?): Boolean

    //가입
    fun join(name: String, email: String, profile_url: String?): Boolean

    suspend fun loadGalleryLoad(context: Context): String?
    suspend fun uploadToFireStorage(context: Context, user_id: String, stream: InputStream): String?
}