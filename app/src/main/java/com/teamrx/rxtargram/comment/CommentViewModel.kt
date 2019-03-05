package com.teamrx.rxtargram.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.repository.AppDataSource

class CommentViewModel(appDataSource: AppDataSource): BaseViewModel(appDataSource) {

    private val commentDTOLiveData: MutableLiveData<List<CommentDTO>> = MutableLiveData()
    private val addCommentResult: MutableLiveData<Boolean> = MutableLiveData()

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

}