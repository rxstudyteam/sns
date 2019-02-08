package com.teamrx.rxtargram.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.repository.AppDataSource

class DetailViewModel(dataSource: AppDataSource): BaseViewModel(dataSource) {

    private val postDTOLiveData: MutableLiveData<List<PostDTO>> by lazy { MutableLiveData<List<PostDTO>>() }

    fun getPosts(): LiveData<List<PostDTO>> = postDTOLiveData
    fun loadPosts() {
        dataSource.getPosts { posts ->
            postDTOLiveData.value = posts
        }
    }
}