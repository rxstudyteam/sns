package com.teamrx.rxtargram.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.teamrx.rxtargram.model.Post

object RemoteAppDataSource: AppDataSource {

    private lateinit var postLiveData: MutableLiveData<List<Post>>

    // https://github.com/kunny/RxFirebase
    // Firebase + Rxjava를 이용해 Obserbable을 리턴하고 ViewModel에서 Livedata로 데이터를 관리하고 싶었으나
    // 위 라이브러리의 기능이 얼만큼 있는지 확인이 안되어 Repo에서 LiveData로 관리하도록 구현함
    override fun getPosts(): LiveData<List<Post>> {
        if(!::postLiveData.isInitialized) {
            postLiveData = MutableLiveData()

            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("post").orderBy("created_at")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if(querySnapshot == null) return@addSnapshotListener

                    val posts = mutableListOf<Post>()
                    for(snapshot in querySnapshot.documents) {
                        try {
                            val item = snapshot.toObject(Post::class.java)
                            // 팔로우한 유저만 구분.
                            if (item != null)
                                posts.add(item)
                        } catch (e: Exception) {
                            firebaseFirestoreException?.printStackTrace()
                            e.printStackTrace()
                        }
                    }

                    postLiveData.postValue(posts)
                }
        }

        return postLiveData
    }
}