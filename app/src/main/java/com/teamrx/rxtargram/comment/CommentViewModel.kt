package com.teamrx.rxtargram.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.repository.AppDataSource

class CommentViewModel(appDataSource: AppDataSource) : BaseViewModel(appDataSource) {
    private val commentDTOLiveData: MutableLiveData<List<CommentDTO>> = MutableLiveData()
//    private val addCommentResult: MutableLiveData<Boolean> = MutableLiveData()
//    private val modifyCommentResult: MutableLiveData<Boolean> = MutableLiveData()
//    private val deleteCommentResult: MutableLiveData<Boolean> = MutableLiveData()

    fun getComments(): LiveData<List<CommentDTO>> = commentDTOLiveData
    fun loadComments(parent_post_id: String) {
        dataSource.setCommentSnapshotListener(parent_post_id) { comments ->
            commentDTOLiveData.value = comments
        }
    }

    suspend fun addComment(parent_post_id: String, user_id: String, content: String): Boolean {
        return dataSource.addComment(parent_post_id, user_id, content)
    }

    suspend fun modifyComment(post_id: String, comment: CommentDTO): Boolean {
        return dataSource.modifyComment(post_id, comment)
    }

    suspend fun deleteComment(post_id: String): Boolean {
        return dataSource.deleteComment(post_id)
    }
}