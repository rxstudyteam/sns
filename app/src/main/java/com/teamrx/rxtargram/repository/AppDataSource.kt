package com.teamrx.rxtargram.repository

import androidx.lifecycle.LiveData
import com.teamrx.rxtargram.model.Post

interface AppDataSource {
    // 글 목록 가져오기
    fun getPosts(): LiveData<List<Post>>
}