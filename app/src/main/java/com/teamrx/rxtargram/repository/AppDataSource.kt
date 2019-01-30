package com.teamrx.rxtargram.repository

import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.model.Post

interface AppDataSource {
    // 글 목록 가져오기
    fun getPosts(callback: (List<Post>) -> Unit)

    // 댓글 목록 가져오기
    fun getComments(post_id: String, callback: (List<CommentDTO>) -> Unit)
}