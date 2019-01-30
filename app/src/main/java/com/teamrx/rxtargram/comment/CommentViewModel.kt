package com.teamrx.rxtargram.comment

import androidx.lifecycle.MutableLiveData
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.repository.AppDataSource

class CommentViewModel(appDataSource: AppDataSource): BaseViewModel(appDataSource) {

    val commentLiveData =  MutableLiveData<List<CommentDTO>>()
    fun getComments(post_id: String) {
        dataSource.getComments(post_id) { comments ->
            commentLiveData.value = comments
        }
    }

}