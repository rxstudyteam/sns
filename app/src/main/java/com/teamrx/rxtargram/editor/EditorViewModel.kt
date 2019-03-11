@file:Suppress("LocalVariableName")

package com.teamrx.rxtargram.editor

import android.content.Context
import android.graphics.Bitmap
import android.log.Log
import android.util.jpegstream
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.repository.AppDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import smart.base.PP

class EditorViewModel(private var dataSource: AppDataSource) : ViewModel() {

    val TAG = EditorViewModel::class.java.simpleName

//    private val fireStore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val userid: String by lazy { PP.user_id.get(PP.deviceid)!! }
    val postImageUrl: MutableLiveData<String> = MutableLiveData()

    fun getPostImage(context: Context) = CoroutineScope(Dispatchers.Main).launch {
        postImageUrl.value = dataSource.loadGalleryLoad(context)
    }

    suspend fun createPost(title: String, content: String, bitmap: Bitmap?) {
        Log.e(title , content , bitmap )
        val image_id: String? = bitmap?.run { dataSource.uploadToFireStoragePostImage(jpegstream) }
        val url = image_id?.let { dataSource.getDownloadUrl(it) }
        val post = PostDTO(userid, title, content, url?.let { listOf(it) })
        val id = dataSource.createPost(post)
        Log.i(TAG, "createPost: $id")
    }
}
