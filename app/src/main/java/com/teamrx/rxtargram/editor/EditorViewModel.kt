package com.teamrx.rxtargram.editor

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.util.toStream
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.repository.AppDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import smart.base.PP

class EditorViewModel(private var dataSource: AppDataSource) : ViewModel() {

    val TAG = EditorViewModel::class.java.simpleName

    private val fireStore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val userId by lazy { PP.user_id.get() }
    val postImageUrl: MutableLiveData<String> = MutableLiveData()

    suspend fun createPost(content: String?, parent_post_no: String?, title: String?, bitmap: Bitmap?) {
        userId?.let {
            val post = PostDTO(it, title, content, parent_post_no)
            val id = dataSource.createPost(post)
            bitmap?.let {
                dataSource.uploadToFireStoragePostImage(id, it.toStream())
                Log.i(TAG, "createPost: image upload::  images/${id}")
            }
            Log.i(TAG, "createPost: $id")
        }
    }

    fun getPostImage(context: Context) {
        CoroutineScope(Dispatchers.Main).launch {
            postImageUrl.value = dataSource.loadGalleryLoad(context)
        }
    }

    suspend fun createPost(title: String, content: String, bitmap: Bitmap?) {
        userId?.let { userid ->
            val post = PostDTO(userid, title, content)
            val id = dataSource.createPost(post)
            bitmap?.let {
                dataSource.uploadToFireStoragePostImage(id, it.toStream())
                Log.i(TAG, "createPost: image upload::  images/${id}")
            }
            Log.i(TAG, "createPost: $id")
        }
    }

}
