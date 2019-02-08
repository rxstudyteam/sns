package com.teamrx.rxtargram.repository

import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.model.PostDTO

class AppRepository(private val remoteAppDataSource: RemoteAppDataSource): AppDataSource {

    override fun getPosts(callback: (List<PostDTO>) -> Unit) {
        remoteAppDataSource.getPosts(callback)
    }

    override fun getPostById(post_id: String, callback: (PostDTO) -> Unit) {
        remoteAppDataSource.getPostById(post_id, callback)
    }

    override fun getComments(post_id: String, callback: (List<CommentDTO>) -> Unit) {
        remoteAppDataSource.getComments(post_id, callback)
    }

    companion object {
        private var INSTANCE: AppRepository? = null
        fun getInstance(remoteDataSource: RemoteAppDataSource) = INSTANCE ?: synchronized(AppRepository::class.java) {
            INSTANCE ?: AppRepository(remoteDataSource).also { INSTANCE = it }
        }
    }
}