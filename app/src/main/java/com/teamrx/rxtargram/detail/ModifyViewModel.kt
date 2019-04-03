package com.teamrx.rxtargram.detail

import androidx.lifecycle.MutableLiveData
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.repository.AppDataSource

class ModifyViewModel(dataSource: AppDataSource) : BaseViewModel(dataSource) {

    val post: MutableLiveData<PostDTO> = MutableLiveData()

    fun getPost(post_id: String) : MutableLiveData<PostDTO> {
        dataSource.getPost(post_id) { post ->
            this.post.value = post
        }
        return post
    }

    fun setPost(post: PostDTO, callback: (Boolean) -> Unit) = dataSource.setPost(post, callback)

}