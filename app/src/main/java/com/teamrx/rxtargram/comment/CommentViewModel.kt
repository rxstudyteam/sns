package com.teamrx.rxtargram.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.repository.AppDataSource

class CommentViewModel(appDataSource: AppDataSource): BaseViewModel(appDataSource) {

    private val commentDTOLiveData: MutableLiveData<List<CommentDTO>> by lazy { MutableLiveData<List<CommentDTO>>() }

    fun getComments(): LiveData<List<CommentDTO>> = commentDTOLiveData
    fun loadComments(post_id: String) {
        dataSource.getComments(post_id) { comments ->
            commentDTOLiveData.value = comments
        }
    }

}