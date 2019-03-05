package com.teamrx.rxtargram.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.repository.AppDataSource

class CommentViewModel(appDataSource: AppDataSource): BaseViewModel(appDataSource) {

    private val commentDTOLiveData: MutableLiveData<List<CommentDTO>> = MutableLiveData()
    private val addCommentResult: MutableLiveData<Boolean> = MutableLiveData()
    private val modifyCommentResult: MutableLiveData<Boolean> = MutableLiveData()
    private val deleteCommentResult: MutableLiveData<Boolean> = MutableLiveData()

    fun getComments(): LiveData<List<CommentDTO>> = commentDTOLiveData
    suspend fun loadComments(post_id: String) {
        val channel = dataSource.getComments(post_id)
        for(list in channel) {
            println("current thread : [${Thread.currentThread().name}]")
            commentDTOLiveData.value = list
        }
    }

    suspend fun addComment(parent_post_id: String, user_id: String, content: String): LiveData<Boolean> {
        addCommentResult.value = dataSource.addComment(parent_post_id, user_id, content)
        return addCommentResult
    }

    suspend fun modifyComment(comment: CommentDTO): LiveData<Boolean> {
        // 값이 변경되면 리스트에 반영
        modifyCommentResult.value = dataSource.modifyComment(comment)
        return modifyCommentResult
    }

    suspend fun deleteComment(post_id: String): LiveData<Boolean> {
        // 값이 삭제되면 리스트에 반영
        deleteCommentResult.value = dataSource.deleteComment(post_id)
        return deleteCommentResult
    }

}