package com.teamrx.rxtargram.repository

import android.log.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.teamrx.rxtargram.model.Post
import com.teamrx.rxtargram.model.ProfileModel
import smart.base.PP
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object RemoteAppDataSource : AppDataSource {

    const val USER_COLLECTION = "user"
    const val POST_COLLECTION = "post"

    object USER_DOCUMENT {
        val EMAIL = "email"
        val NAME = "name"
        val PROFILE_URL = "profile_url"
    }

    object POST_DOCUMENT {
        val CREATED_AT = "created_at"
    }

    //    override fun getProfile(user_id: String): ProfileModel? {
//        val future = Executors.newSingleThreadExecutor().submit<ProfileModel> {
//            val task = FirebaseFirestore.getInstance()
//                    .collection(USER_COLLECTION).document(user_id)
//                    .get()
//            val document = Tasks.await(task, 5, TimeUnit.SECONDS)
//
//            if (document.exists())
//                document.toObject(ProfileModel::class.java)
//            else
//                null
//        }
//        return future.get(5, TimeUnit.SECONDS)
//    }
    override fun getProfile(user_id: String): ProfileModel? = try {
        Executors.newSingleThreadExecutor().submit<ProfileModel> {
            val db = FirebaseFirestore.getInstance()
            var task = db.collection(USER_COLLECTION).document(user_id).get()
            val document = Tasks.await(task, 5, TimeUnit.SECONDS)

            if (document.exists())
                document.toObject(ProfileModel::class.java)
            else
                null
        }.get(5, TimeUnit.SECONDS)
    } catch (e: Exception) {
        null
    }

    override fun setProfile(value: ProfileModel): Boolean {
        val userId = PP.user_id.get()
        if (userId.isNullOrEmpty())
            throw IllegalArgumentException("PP.user_id.get() is empty")

        val db = FirebaseFirestore.getInstance()
        val task = db.collection(USER_COLLECTION).document(userId)
                .update(USER_DOCUMENT.NAME, value.name,
                        USER_DOCUMENT.EMAIL, value.email,
                        USER_DOCUMENT.PROFILE_URL, value.profile_url
                )

        task.addOnCompleteListener {
            Log.d("addOnCompleteListener")
        }.addOnSuccessListener {
            Log.d("addOnSuccessListener")
        }.addOnFailureListener {
            Log.d("addOnFailureListener")
        }

        Executors.newSingleThreadExecutor().submit {
            Tasks.await(task, 5, TimeUnit.SECONDS)
        }.get(5, TimeUnit.SECONDS)
        return true
    }

    override fun join(profileModel: ProfileModel): Boolean {
        FirebaseFirestore.getInstance().collection(USER_COLLECTION)
                .add(profileModel)
                .addOnSuccessListener { documentReference ->
                    PP.user_id.set(documentReference.id)
                    Log.e(PP.user_id.get(), "회원가입이 완료됨")
                }
                .addOnFailureListener { e -> e.printStackTrace() }
        return true
    }

    // https://github.com/kunny/RxFirebase
// Firebase + Rxjava를 이용해 Obserbable을 리턴하고 ViewModel에서 Livedata로 데이터를 관리하고 싶었으나
// 위 라이브러리의 기능이 얼만큼 있는지 확인이 안되어 Repo에서 LiveData로 관리하도록 구현함
    override fun getPosts(): LiveData<List<Post>> {
        val postLiveData = MutableLiveData<List<Post>>()

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection(POST_COLLECTION).orderBy(POST_DOCUMENT.CREATED_AT)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (querySnapshot == null) return@addSnapshotListener

                    val posts = mutableListOf<Post>()
                    for (snapshot in querySnapshot.documents) {
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

        return postLiveData
    }
}