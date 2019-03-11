package com.teamrx.rxtargram.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.model.PostImages
import com.teamrx.rxtargram.repository.AppDataSource

class DetailViewModel(dataSource: AppDataSource) : BaseViewModel(dataSource) {

    val posts: MutableLiveData<List<PostDTO>> = MutableLiveData()

    private val post: MutableLiveData<PostDTO> by lazy { MutableLiveData<PostDTO>() }

    fun postsListen() {
        dataSource.getPosts {
            posts.value = it
        }
    }

    fun getPostById(): LiveData<PostDTO> = post
    fun loadPostById(post_id: String) {
        dataSource.getPostById(post_id) { post ->
            this.post.value = post
        }
    }

    fun modifyPost(post: PostDTO, callback: (Boolean) -> Unit) = dataSource.modifyPost(post, callback)
}