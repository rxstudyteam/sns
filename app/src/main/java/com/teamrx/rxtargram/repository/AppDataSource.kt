package com.teamrx.rxtargram.repository

import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.model.PostDTO

interface AppDataSource {
    // 글 목록 가져오기
    fun getPosts(callback: (List<PostDTO>) -> Unit)

    // 댓글 목록 가져오기
    fun getComments(post_id: String, callback: (List<CommentDTO>) -> Unit)
}