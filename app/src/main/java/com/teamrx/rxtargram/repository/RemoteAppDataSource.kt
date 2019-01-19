package com.teamrx.rxtargram.repository

import android.log.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.teamrx.rxtargram.model.Post
import com.teamrx.rxtargram.model.ProfileModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import smart.base.PP

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

    override fun getProfile(user_id: String, callback: (ProfileModel) -> Unit) {
        var user_id = "KxUypfZKf2cKmJs4jOeU"
        FirebaseFirestore.getInstance()
            .collection(USER_COLLECTION).document(user_id)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result!!
                    if (document.exists()) {
                        Log.w(document.id, document.data)
                        callback(ProfileModel(document[USER_DOCUMENT.EMAIL] as String, document[USER_DOCUMENT.NAME] as String, document[USER_DOCUMENT.PROFILE_URL] as String?))
                    }
                }
            }
    }

    override fun getProfile2(user_id: String): ProfileModel? {
        var user_id = "KxUypfZKf2cKmJs4jOeU"
        var profileModel: ProfileModel? = null
        Log.e()
        runBlocking {
            Log.e()
            Log.e()
            FirebaseFirestore.getInstance()
                .collection(USER_COLLECTION).document(user_id)
                .get()
                .addOnCompleteListener { task ->
                    var job = launch {
                        Log.e()
                        if (task.isSuccessful) {
                            Log.e()
                            val document = task.result!!
                            if (document.exists()) {
                                Log.e()
                                Log.w(document.id, document.data)
                                profileModel = ProfileModel(document[USER_DOCUMENT.NAME] as String, document[USER_DOCUMENT.EMAIL] as String, document[USER_DOCUMENT.PROFILE_URL] as String?)
                            }
                            Log.e(profileModel)
                        }

                        Log.e(profileModel)
                    }
//                    job.join()
                    Log.e(profileModel)
                }
            Log.e(profileModel)
        }
        Log.e(profileModel)
        return profileModel
    }

    override fun join(profileModel: ProfileModel) {
        FirebaseFirestore.getInstance().collection(USER_COLLECTION)
            .add(profileModel)
            .addOnSuccessListener { documentReference ->
                PP.user_id.set(documentReference.id)
                Log.e(PP.user_id.get(), "회원가입이 완료됨")
            }
            .addOnFailureListener { e -> e.printStackTrace() }
    }

//    override fun setProfile(profile: MutableLiveData<MProfile?>) {
//        FirebaseFirestore.getInstance().collection(USER_COLLECTION).document(PP.user_id.get())
//                .get()
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        var document = task.result!!
//                        Log.d(document.id, document.data)
//                        profile.value = MProfile(document[USER.EMAIL] as String, document[USER.NAME] as String, document[USER.PROFILE_URL] as String?)
//                    } else {
//                        Log.w(task.exception)
//                    }
//                }
//                .addOnFailureListener { e -> e.printStackTrace() }
//    }
//
//    fun getProfles(callback: (List<Pair<String, MProfile>>) -> Unit) {
//        //목록
//        FirebaseFirestore.getInstance().collection(USER_COLLECTION)
//                .get()
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        val list: List<Pair<String, MProfile>> = ArrayList()
//                        for (document in task.result!!) {
//                            Log.d(document.id, document.data, MProfile(document[USER.EMAIL] as String, document[USER.NAME] as String, document[USER.PROFILE_URL] as String?))
//                            list + document.id to MProfile(document[USER.EMAIL] as String, document[USER.NAME] as String, document[USER.PROFILE_URL] as String?)
//                        }
//                        callback(list)
//                    } else {
//                        Log.w(task.exception)
//                    }
//                }
//                .addOnFailureListener { e -> e.printStackTrace() }
//    }

//        val database = FirebaseDatabase.getInstance()
//        val ref = database.getReference().g
//        ref.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // This method is called once with the initial value and again whenever data at this location is updated.
//                val value = dataSnapshot.getValue(String::class.java)
//                Log.e(value)
//            }
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Log.e(error)
//            }
//        })
//    }

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