package com.teamrx.rxtargram.repository

import androidx.lifecycle.LiveData
import com.teamrx.rxtargram.model.ProfileModel
import com.teamrx.rxtargram.model.Post

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
    fun setProfile(value: ProfileModel) : Boolean
    //가입
    fun join(profileModel: ProfileModel): Boolean


}