package com.teamrx.rxtargram.editor

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.teamrx.rxtargram.model.PostConst
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.repository.AppDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditorViewModel(private var dataSource: AppDataSource) : ViewModel() {

    val TAG = EditorViewModel::class.java.simpleName

    private val fireStore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    var userId: String? = null
    val postImageUrl: MutableLiveData<String> = MutableLiveData()

    fun createPost(content: String?, parent_post_no: String?, title: String?) {
        userId?.let {
            val post = PostDTO(it, parent_post_no, title, content, Timestamp.now().toDate())
            fireStore.collection(PostConst.POST_COLLECTION).document().set(post)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i(TAG, "success")
                        // 생성된 document name 은 어떻게 가져오지?
                    }
                }
        }
    }

    fun getPostImage(view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            postImageUrl.value = dataSource.loadGalleryLoad(view.context)
        }
    }

}
