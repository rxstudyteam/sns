package com.teamrx.rxtargram.detail

import androidx.lifecycle.LiveData
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.model.Post
import com.teamrx.rxtargram.repository.AppDataSource

class DetailViewModel(dataSource: AppDataSource) : BaseViewModel(dataSource) {

    fun getPosts(): LiveData<List<Post>> = dataSource.getPosts()

    fun modifyPost(post: Post) = dataSource.modifyPost(post)
}