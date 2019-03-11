package com.teamrx.rxtargram.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.model.PostImages
import com.teamrx.rxtargram.repository.AppDataSource

class DetailViewModel(dataSource: AppDataSource) : BaseViewModel(dataSource) {

    private val postDTOs: MutableLiveData<List<PostDTO>> by lazy { MutableLiveData<List<PostDTO>>() }
    private val post: MutableLiveData<PostDTO> by lazy { MutableLiveData<PostDTO>() }
    private val postImages: MutableLiveData<List<PostImages>> by lazy { MutableLiveData<List<PostImages>>() }

    fun getPosts(): LiveData<List<PostDTO>> = postDTOs
    fun loadPosts() {
        dataSource.getPosts { posts ->
            postDTOs.value = posts
        }
    }

    fun getPostById(): LiveData<PostDTO> = post
    fun loadPostById(post_id: String) {
        dataSource.getPostById(post_id) { post ->
            this.post.value = post
        }
    }

    fun modifyPost(post: PostDTO, callback: (Boolean) -> Unit) = dataSource.modifyPost(post, callback)

//    fun getPostImages(post_id: String?) {
//        dataSource.getPostImages(post_id) { postImages ->
//            postImages.forEachIndexed { index, postImages ->
//                dataSource.getPostImageUrl(postImages.post_image_id ){
//                    Log.e(it)
//                }
//            }
//            this.postImages.value = postImages
//        }
//    }
}