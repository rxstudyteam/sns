package com.teamrx.rxtargram.detail

import androidx.lifecycle.MutableLiveData
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.model.ProfileModel
import com.teamrx.rxtargram.repository.AppDataSource
import com.teamrx.rxtargram.repository.CachedUser

class DetailViewModel(dataSource: AppDataSource) : BaseViewModel(dataSource) {

    val posts: MutableLiveData<List<PostDTO>> = MutableLiveData()

    fun postsListen() {
        dataSource.setPostSnapshotListener {
            posts.value = it
        }
    }

    suspend fun getProfile(user_id: String): ProfileModel {
        return CachedUser.get(user_id)
                ?: dataSource.getProfile(user_id).also {
                    CachedUser.put(user_id, it)
                }
    }

}

