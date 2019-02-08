package com.teamrx.rxtargram.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.repository.AppDataSource

class DetailViewModel(dataSource: AppDataSource): BaseViewModel(dataSource) {

    private val postDTOs: MutableLiveData<List<PostDTO>> by lazy { MutableLiveData<List<PostDTO>>() }
    private val postDTO: MutableLiveData<PostDTO> by lazy { MutableLiveData<PostDTO>() }

    fun getPosts(): LiveData<List<PostDTO>> = postDTOs
    fun loadPosts() {
        dataSource.getPosts { posts ->
            postDTOs.value = posts
        }
    }

    fun getPostById(): LiveData<PostDTO> = postDTO
    fun loadPostById(post_id: String) {
        dataSource.getPostById(post_id) { post ->
            postDTO.value = post
        }
    }
}