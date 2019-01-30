package com.teamrx.rxtargram.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.model.Post

object RemoteAppDataSource: AppDataSource {

    private val fireStore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun getPosts(callback: (List<Post>) -> Unit) {

        fireStore.collection("post").orderBy("created_at")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(querySnapshot == null) return@addSnapshotListener

                val posts = mutableListOf<Post>()
                for(snapshot in querySnapshot.documents) {
                    try {
                        val item = snapshot.toObject(Post::class.java)

                        // FIXME check
                        item?.post_id = snapshot.id

                        // 팔로우한 유저만 구분.
                        if (item != null) {
                            posts.add(item)
                        }
                    } catch (e: Exception) {
                        firebaseFirestoreException?.printStackTrace()
                        e.printStackTrace()
                    }
                }

                callback(posts)
            }
    }

    override fun getComments(post_id: String, callback: (List<CommentDTO>) -> Unit) {

        fireStore.collection("post").whereEqualTo("parent_post_no", post_id).get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    task.result?.let { querySnapshot ->
                        val commentDTOs = mutableListOf<CommentDTO>()

                        for(dc in querySnapshot.documents) {
                            val item = dc.toObject(CommentDTO::class.java)

                            if(item?.parent_post_no == post_id) {
                                commentDTOs.add(item)
                            }
                        }

                        callback(commentDTOs)
                    }

                }
            }
    }
}