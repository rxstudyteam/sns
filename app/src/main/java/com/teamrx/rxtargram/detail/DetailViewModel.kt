package com.teamrx.rxtargram.detail

import androidx.lifecycle.MutableLiveData
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.model.Post
import com.teamrx.rxtargram.repository.AppDataSource

class DetailViewModel(dataSource: AppDataSource): BaseViewModel(dataSource) {

    val postLiveData: MutableLiveData<List<Post>> by lazy { MutableLiveData<List<Post>>() }
    fun getPosts() {
        dataSource.getPosts { posts ->
            postLiveData.value = posts
        }
    }
}