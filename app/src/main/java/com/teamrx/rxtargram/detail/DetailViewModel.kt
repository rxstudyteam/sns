package com.teamrx.rxtargram.detail

import androidx.lifecycle.MutableLiveData
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.repository.AppDataSource

class DetailViewModel(dataSource: AppDataSource) : BaseViewModel(dataSource) {

    val posts: MutableLiveData<List<PostDTO>> = MutableLiveData()

    fun postsListen() {
        dataSource.getPosts {
            posts.value = it
        }
    }

}