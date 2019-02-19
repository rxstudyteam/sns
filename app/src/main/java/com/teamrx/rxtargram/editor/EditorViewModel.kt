package com.teamrx.rxtargram.editor

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.profile.toStream
import com.teamrx.rxtargram.repository.AppDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditorViewModel(private var dataSource: AppDataSource) : ViewModel() {

    val TAG = EditorViewModel::class.java.simpleName

    private val fireStore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    var userId: String? = null
    val postImageUrl: MutableLiveData<String> = MutableLiveData()

    fun createPost(content: String?, parent_post_no: String?, title: String?, bitmap: Bitmap?, callback: () -> Unit) {
        userId?.let {
            CoroutineScope(Dispatchers.Main).launch {
                val post = PostDTO(it, parent_post_no, title, content, Timestamp.now().toDate())
                val id = dataSource.createPost(post)
                bitmap?.let {
                    dataSource.uploadToFireStoragePostImage(id, it.toStream())
                    Log.i(TAG, "createPost: image upload::  images/${id}")
                }
                Log.i(TAG, "createPost: $id")
                callback()
            }
        }
    }

    fun getPostImage(view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            postImageUrl.value = dataSource.loadGalleryLoad(view.context)
        }
    }

}
